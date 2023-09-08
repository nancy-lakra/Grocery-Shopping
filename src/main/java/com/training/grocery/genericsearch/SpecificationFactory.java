package com.training.grocery.genericsearch;

import java.util.Collections;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

@Component
public class SpecificationFactory<T> {

	public Specification<T> isEqual(String key, Object arg) {
		GenericSpecificationBuilder<T> builder = new GenericSpecificationBuilder<>();
		return builder.with(key, SearchOperation.EQUALITY, Collections.singletonList(arg)).build();
	}

	public Specification<T> isGreaterThan(String key, Comparable arg) {
		GenericSpecificationBuilder<T> builder = new GenericSpecificationBuilder<>();
		return builder.with(key, SearchOperation.GREATER_THAN, Collections.singletonList(arg)).build();
	}

	public Specification<T> isLike(String key, Object arg) {
		GenericSpecificationBuilder<T> builder = new GenericSpecificationBuilder<>();
		return builder.with(key, SearchOperation.LIKE, Collections.singletonList(arg)).build();
	}

	public Specification<T> isIN(String key, Object arg) {
		GenericSpecificationBuilder<T> builder = new GenericSpecificationBuilder<>();
		return builder.with(key, SearchOperation.IN, Collections.singletonList(arg)).build();
	}

	public Specification<T> isLessThan(String key, Comparable arg) {
		GenericSpecificationBuilder<T> builder = new GenericSpecificationBuilder<>();
		return builder.with(key, SearchOperation.LESS_THAN, Collections.singletonList(arg)).build();
	}

	public Specification<T> isLessThanEqualTo(String key, Comparable arg) {
		GenericSpecificationBuilder<T> builder = new GenericSpecificationBuilder<>();
		return builder.with(key, SearchOperation.LESS_THAN_EQUAL, Collections.singletonList(arg)).build();
	}

	public Specification<T> isGreaterThanEqualTo(String key, Comparable arg) {
		GenericSpecificationBuilder<T> builder = new GenericSpecificationBuilder<>();
		return builder.with(key, SearchOperation.GREATER_THAN_EQUAL, Collections.singletonList(arg)).build();
	}
}
