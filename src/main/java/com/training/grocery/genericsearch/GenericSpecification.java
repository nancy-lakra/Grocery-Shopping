package com.training.grocery.genericsearch;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class GenericSpecification<T> implements Specification<T> {

	private SearchCriteria searchCriteria;

	public GenericSpecification(final SearchCriteria searchCriteria) {
		super();
		this.searchCriteria = searchCriteria;
	}

	@Override
	public Predicate toPredicate(Root<T> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
		List<Object> arguments = searchCriteria.getArguments();
		Object arg = arguments.get(0);

		switch (searchCriteria.getSearchOperation()) {
			case EQUALITY:
				return criteriaBuilder.equal(root.get(searchCriteria.getKey()), arg);
			case GREATER_THAN:
				return criteriaBuilder.greaterThan(root.get(searchCriteria.getKey()), (Comparable) arg);
			case IN:
				return root.get(searchCriteria.getKey()).in(arguments);
			case LESS_THAN:
				return criteriaBuilder.lessThan(root.get(searchCriteria.getKey()), (Comparable) arg);
			case GREATER_THAN_EQUAL:
				return criteriaBuilder.greaterThanOrEqualTo(root.get(searchCriteria.getKey()), (Comparable) arg);
			case LESS_THAN_EQUAL:
				return criteriaBuilder.lessThanOrEqualTo(root.get(searchCriteria.getKey()), (Comparable) arg);
			default:
				return null;
		}
	}
}
