package com.training.grocery.basket.dbmodel;

import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import org.hibernate.envers.Audited;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Builder
@Data
@Table(name = "Basket")
@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Basket {

	@Column(name = "id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	@Column(name = "userid")
	String userid;

	@Column(name = "totalamount")
	float totalamount;

	@Column(name = "totaldiscount")
	float totaldiscount;

	@Column(name = "productinbasket", columnDefinition = "jsonb")
	@Type(type = "jsonb")
	@Basic(fetch = FetchType.LAZY)
	List<ProductInfo> products;


}
