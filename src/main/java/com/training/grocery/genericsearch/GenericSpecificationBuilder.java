package com.training.grocery.genericsearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.springframework.data.jpa.domain.Specification;


public class GenericSpecificationBuilder<T> {

	private final List<SearchCriteria> params;
	private final List<Specification<T>> specifications;

	public GenericSpecificationBuilder() {
		this.params = new ArrayList<>();
		this.specifications = new ArrayList<>();
	}

	public final GenericSpecificationBuilder<T> with(final String key, final SearchOperation searchOperation,
			final List<Object> arguments) {
		return with(key, searchOperation, false, arguments);
	}

	public final GenericSpecificationBuilder<T> with(final String key, final SearchOperation searchOperation,
			final boolean isOrOperation, final List<Object> arguments) {
		params.add(new SearchCriteria(key, searchOperation, isOrOperation, arguments));
		return this;
	}

	public final GenericSpecificationBuilder<T> with(Specification<T> specification) {
		specifications.add(specification);
		return this;
	}

	public Specification<T> build() {
		Specification<T> result = null;
		if (!params.isEmpty()) {
			result = new GenericSpecification<>(params.get(0));
			for (int index = 1; index < params.size(); ++index) {
				SearchCriteria searchCriteria = params.get(index);
				result = searchCriteria.isOrOperation()
						? Specification.where(result).or(new GenericSpecification<>(searchCriteria))
						: Specification.where(result).and(new GenericSpecification<>(searchCriteria));
			}
		}
		if (!specifications.isEmpty()) {
			int index = 0;
			if (Objects.isNull(result)) {
				result = specifications.get(index++);
			}
			for (; index < specifications.size(); ++index) {
				result = Specification.where(result).and(specifications.get(index));
			}
		}
		return result;
	}
}
