package com.training.grocery.payment.hibernate;

import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.genericsearch.GenericSpecificationBuilder;
import com.training.grocery.genericsearch.SortType;
import com.training.grocery.payment.datamodel.PaymentType;
import com.training.grocery.payment.dbmodel.Payment;
import com.training.grocery.payment.dbmodel.Payment.PaymentBuilder;
import com.training.grocery.payment.searchReq.PaymentReq;
import com.training.grocery.walletpay.dbmodel.Wallet;
import com.training.grocery.walletpay.hibernate.WalletRepository;


@Service
public class PaymentService {

	Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	PaymentRepository paymentRepo;
	@Autowired
	WalletRepository walletRepo;
	@Autowired
	private com.training.grocery.genericsearch.SpecificationFactory<Payment> paymentSpecificationFactory;

	public void transfer(String fromid, String toid, long orderid, float amount, String comment, String paytype) {

		PaymentBuilder paybuild = Payment.builder().orderid(orderid).amount(amount).date(LocalDateTime.now())
				.fromid(fromid).toid(toid).comment(comment).paymenttype(paytype);
		if (!PaymentType.WITHDRAW.toString().equals(paytype)) {
			Wallet to = walletRepo.findByUserid(toid);

			if (PaymentType.TOPUP.toString().equals(paytype)) {

				to.setAmount(to.getAmount() + amount);
				walletRepo.save(to);
			} else {
				Wallet from = walletRepo.findByUserid(fromid);
				float balance = from.getAmount();
				if (balance < amount) {
					Payment paymentDetail = paybuild.status("FAILED").build();
					paymentRepo.save(paymentDetail);
					log.warn("Insufficient Balance cannot transfer from wallet {} to {}", fromid, toid);
					throw new HttpMessageNotReadableException(EnumsForExceptions.Insufficient_Balance.toString());
				}
				from.setAmount(balance - amount);
				to.setAmount(to.getAmount() + amount);
				walletRepo.save(to);
				walletRepo.save(from);
			}
		} else if (PaymentType.WITHDRAW.toString().equals(paytype)) {
			Wallet user = walletRepo.findByUserid(fromid);
			float balance = user.getAmount();

			if (balance < amount) {
				Payment paymentDetail = paybuild.status("FAILED").build();
				paymentRepo.save(paymentDetail);
				log.warn("Insufficient Balance cannot transfer from wallet {} to {}", fromid, toid);
				throw new HttpMessageNotReadableException(EnumsForExceptions.Insufficient_Balance.toString());
			}
			user.setAmount(balance - amount);
			walletRepo.save(user);
		}
		Payment paymentDetail = paybuild.status("SUCCESS").build();
		log.info("Amount transfered from wallet {} to {}", fromid, toid);
		paymentRepo.save(paymentDetail);
	}

	public Page<Payment> searchPayments(String userid, PaymentReq paymentReq) {
		GenericSpecificationBuilder<Payment> builder = new GenericSpecificationBuilder<>();
		builder.with(paymentSpecificationFactory.isEqual("fromid", Long.parseLong(userid)));
		builder.with(paymentSpecificationFactory.isEqual("toid", Long.parseLong(userid)));
		if (!StringUtils.isEmpty(paymentReq.getAmount())) {
			builder.with(paymentSpecificationFactory.isIN("amount", paymentReq.getAmount()));
		}
		if (!StringUtils.isEmpty(paymentReq.getOrderId())) {
			builder.with(paymentSpecificationFactory.isIN("orderid", paymentReq.getOrderId()));
		}
		if (!StringUtils.isEmpty(paymentReq.getType())) {
			builder.with(paymentSpecificationFactory.isIN("type", paymentReq.getType()));
		}
		if (!StringUtils.isEmpty(paymentReq.getStatus())) {
			builder.with(paymentSpecificationFactory.isIN("status", paymentReq.getStatus()));
		}
		if (!StringUtils.isEmpty(paymentReq.getLowerTime())) {
			builder.with(paymentSpecificationFactory.isGreaterThanEqualTo("time", paymentReq.getLowerTime()));
		}
		if (!StringUtils.isEmpty(paymentReq.getUpperTime())) {
			builder.with(paymentSpecificationFactory.isLessThanEqualTo("time", paymentReq.getUpperTime()));
		}
		Pageable pageable;
		if (!StringUtils.isEmpty(paymentReq.getSortType())) {
			if (paymentReq.getSortType().equals(SortType.ASC)) {
				pageable = PageRequest.of(paymentReq.getPage(), paymentReq.getSize(),
						Sort.by(paymentReq.getSortBy()).ascending());
			} else {
				pageable = PageRequest.of(paymentReq.getPage(), paymentReq.getSize(),
						Sort.by(paymentReq.getSortBy()).descending());
			}
		} else {
			pageable = PageRequest.of(paymentReq.getPage(), paymentReq.getSize(), Sort.by(paymentReq.getSortBy()));
		}
		try {
			return paymentRepo.findAll(builder.build(), pageable);
		} catch (Exception e) {
			throw new HttpMessageNotReadableException(EnumsForExceptions.Bad_Request.toString());
		}
	}
}
