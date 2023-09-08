package com.training.grocery.order.hibernate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.training.grocery.basket.dbmodel.ProductInfo;
import com.training.grocery.basket.hibernate.BasketService;
import com.training.grocery.exceptionhandling.EnumsForExceptions;
import com.training.grocery.exceptionhandling.PaymentFailedException;
import com.training.grocery.genericsearch.GenericSpecificationBuilder;
import com.training.grocery.genericsearch.SortType;
import com.training.grocery.jwt.config.GenericFilter;
import com.training.grocery.order.datamodel.Status;
import com.training.grocery.order.dbmodel.DeliveryInfo;
import com.training.grocery.order.dbmodel.Order;
import com.training.grocery.order.requestmodel.OrderHolder;
import com.training.grocery.order.searchReq.OrderReq;
import com.training.grocery.payment.datamodel.PaymentType;
import com.training.grocery.payment.hibernate.PaymentService;
import com.training.grocery.product.hibernate.ProductService;
import com.training.grocery.walletpay.hibernate.WalletService;


@Service
public class OrderService {

	@Autowired
	OrderRepository order_repo;

	@Autowired
	PaymentService paymentService;

	@Autowired
	BasketService basketService;

	@Autowired
	WalletService walletService;

	@Autowired
	ProductService productService;

	@Autowired
	private com.training.grocery.genericsearch.SpecificationFactory<Order> orderSpecificationFactory;

	public Order findById(Long id) {
		return order_repo.findById(id).orElse(null);
	}

	public boolean isInRecords(Long id) {
		Order order = null;
		order = findById(id);
		if (order == null)
			return false;
		return true;
	}

	public String showStatus(Long id) {
		Status s = findById(id).getOrder_status();
		return s.getStatusChar();
	}

	public List<OrderHolder> getAll(String userid) {
		Long id = Long.parseLong(userid);
		List<Order> ol = order_repo.findAll();
		List<OrderHolder> res = new ArrayList<OrderHolder>();
		for (Order order : ol) {
			if (order.getBuyer_id() == id) {
				OrderHolder oh = new OrderHolder(order.getOrder_id(), order.getBuyer_id(), order.getDel_info(),
						order.getProd_list(), order.getOrder_amount(), order.getOrder_status());
				res.add(oh);
			}
		}
		return res;
	}

	public boolean returned(Long id) {
		return true;
		// for now
	}

	public boolean payAllSellers(OrderHolder order) {
		String buyer_id = GenericFilter.threadLocal.get();
		float walletAmount = walletService.getAmount(buyer_id);
		float amount = order.getOrder_amount();
		if (walletAmount < amount)
			return false;
		Long order_id = order.getOrder_id();
		String comment = "CONFIDENTIAL ORDER PAYMENT";
		String payType = PaymentType.PAID_FOR_ORDER.toString();
		List<ProductInfo> prod_list = order.getProd_list();
		HashMap<String, Float> mp = new HashMap<String, Float>();
		for (ProductInfo prod : prod_list) {
			String seller = String.valueOf(productService.findProdById(prod.getProductid()).getUserid());
			if (mp.containsKey(seller)) {
				float amt = mp.get(seller);
				mp.put(seller, amt + prod.getAmount());
			} else {
				mp.put(seller, prod.getAmount());
			}
		}
		for (HashMap.Entry<String, Float> entry : mp.entrySet()) {
			String seller_id = entry.getKey();
			Long amt = Long.parseLong(entry.getKey());
			paymentService.transfer(seller_id, buyer_id, order_id, amt, comment, payType);
		}
		return true;
	}

	public String createOrder(OrderHolder order) throws Exception {
		Order orderObj = new Order(order);
		order_repo.save(orderObj);
		String buyer_id = GenericFilter.threadLocal.get();
		Long order_id = order.getOrder_id();
		updateProdPrices(order);
		boolean paid = payAllSellers(order);
		if (!paid)
			throw new PaymentFailedException(EnumsForExceptions.Insufficient_Balance.toString());
		order.setOrder_status(Status.OUT_FOR_DELIVERY);
		order_repo.save(orderObj);
		basketService.clearBasket(buyer_id);
		return "";
	}

	private void updateProdPrices(OrderHolder order) {
		List<ProductInfo> prod_list = order.getProd_list();
		for (ProductInfo prod : prod_list) {
			prod.setAmount(prod.getQuantity() * productService.findProdById(prod.getProductid()).getPrice());
		}
	}

	public void refund(Long order_id) {
		Order order = findById(order_id);
		String buyer_id = String.valueOf(order.getBuyer_id());
		String comment = "CONFIDENTIAL REFUND";
		String payType = PaymentType.REFUND.toString();
		// assuming that seller wallet cannot be deficient of money as user has paid them in any case
		List<ProductInfo> prod_list = order.getProd_list();
		HashMap<String, Float> mp = new HashMap<String, Float>();
		for (ProductInfo prod : prod_list) {
			String seller = String.valueOf(productService.findProdById(prod.getProductid()).getUserid());
			if (mp.containsKey(seller)) {
				float amt = mp.get(seller);
				mp.put(seller, amt + prod.getAmount());
			} else {
				mp.put(seller, prod.getAmount());
			}
		}
		for (HashMap.Entry<String, Float> entry : mp.entrySet()) {
			String seller_id = entry.getKey();
			Long amt = Long.parseLong(entry.getKey());
			paymentService.transfer(buyer_id, seller_id, order_id, amt, comment, payType);
		}
	}

	public String updateOrder(Long id, Status status) throws Exception {
		Order order = findById(id);
		if (order.getOrder_status().equals(Status.REQUEST_BEING_CONSIDERED))
			throw new EntityExistsException(EnumsForExceptions.Request_already_exists.toString());
		if (status == Status.CANCEL_REQUEST) {
			if (order.getOrder_status().equals(Status.CANCELLED))
				throw new EntityExistsException(EnumsForExceptions.Order_already_cancelled.toString());
			if (order.getOrder_status().equals(Status.DELIVERED))
				throw new EntityExistsException(EnumsForExceptions.Order_already_delivered.toString());
			if (!order.getOrder_status().equals(Status.OUT_FOR_DELIVERY)) {
				refund(id);
			}
			order.setOrder_status(Status.CANCELLED);
			// the payment refund procedure and updating the stock
		} else if (status == Status.RETURN_REQUESTED) {
			if (order.getOrder_status().equals(Status.RETURNED))
				throw new EntityExistsException(EnumsForExceptions.Order_already_returned.toString());
			if (!order.getOrder_status().equals(Status.DELIVERED))
				throw new EntityExistsException(EnumsForExceptions.Order_still_not_delivered.toString());
			LocalDateTime from = order.getDel_info().getDelivery_date_time();
			LocalDateTime to = LocalDateTime.now();
			Duration duration = Duration.between(from, to);
			int MAX_ALLOWED_RETURN_DAYS_LIMIT = 7;
			if (duration.toDays() > MAX_ALLOWED_RETURN_DAYS_LIMIT)
				throw new EntityExistsException(EnumsForExceptions.duration_for_return_expired.toString());
			if (!returned(id))
				throw new EntityExistsException(EnumsForExceptions.Request_already_exists.toString());
			refund(id);
			order.setOrder_status(Status.RETURNED);
		} else if (status == Status.DELIVERED) {
			if (!order.getOrder_status().equals(Status.OUT_FOR_DELIVERY)) {
				order.setOrder_status(Status.DELIVERED);
			}
			DeliveryInfo delinfo = order.getDel_info();
			delinfo.setDelivery_date_time(LocalDateTime.now());
		}
		// TO BE DONE BY TIMER ANNOTATIONS
		order.setLast_processed(LocalDateTime.now());
		order_repo.save(order);
		return "";
	}

	public Page<Order> searchOrder(String userid, OrderReq orderReq) {
		GenericSpecificationBuilder<Order> builder = new GenericSpecificationBuilder<>();
		builder.with(orderSpecificationFactory.isEqual("buyer_id", Long.parseLong(userid)));

		if (!StringUtils.isEmpty(orderReq.getAmount())) {
			builder.with(orderSpecificationFactory.isIN("order_amount", orderReq.getAmount()));
		}
		if (!StringUtils.isEmpty(orderReq.getStatus())) {
			builder.with(orderSpecificationFactory.isIN("order_status", orderReq.getStatus()));
		}
		if (!StringUtils.isEmpty(orderReq.getCreationTime())) {
			builder.with(orderSpecificationFactory.isIN("order_date_time", orderReq.getCreationTime()));
		}
		if (!StringUtils.isEmpty(orderReq.getLastUpdatedTimes())) {
			builder.with(orderSpecificationFactory.isIN("last_processed", orderReq.getLastUpdatedTimes()));
		}
		if (!StringUtils.isEmpty(orderReq.getLowerTime())) {
			builder.with(orderSpecificationFactory.isGreaterThan("order_date_time", orderReq.getLowerTime()));
		}
		if (!StringUtils.isEmpty(orderReq.getUpperTime())) {
			builder.with(orderSpecificationFactory.isLessThan("order_date_time", orderReq.getUpperTime()));
		}
		Pageable pageable;
		if (!StringUtils.isEmpty(orderReq.getSortType())) {
			if (orderReq.getSortType().equals(SortType.ASC)) {
				pageable = PageRequest.of(orderReq.getPage(), orderReq.getSize(),
						Sort.by(orderReq.getSortBy()).ascending());
			} else {
				pageable = PageRequest.of(orderReq.getPage(), orderReq.getSize(),
						Sort.by(orderReq.getSortBy()).descending());
			}
		} else {
			pageable = PageRequest.of(orderReq.getPage(), orderReq.getSize(), Sort.by(orderReq.getSortBy()));
		}

		try {
			return order_repo.findAll(builder.build(), pageable);
		} catch (Exception e) {
			throw new HttpMessageNotReadableException(EnumsForExceptions.Bad_Request.toString());
		}
	}

}
