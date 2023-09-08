package com.training.grocery.payment.dbmodel;


import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Builder
@Data
@Table(name = "Payments")
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Payment {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "orderid")
	Long orderid;

	@Column(name = "fromid")
	String fromid;

	@Column(name = "toid")
	String toid;

	@Column(name = "amount")
	Float amount;

	@Column(name = "type")
	String paymenttype;

	@Column(name = "status")
	String status;

	@Column(name = "comment")
	String comment;

	@Column(name = "time")
	LocalDateTime date;
}
