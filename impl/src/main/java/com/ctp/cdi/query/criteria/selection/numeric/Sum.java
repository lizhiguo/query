package com.ctp.cdi.query.criteria.selection.numeric;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.SingularAttribute;

import com.ctp.cdi.query.criteria.selection.SingularAttributeSelection;

public class Sum<P, X extends Number> extends SingularAttributeSelection<P, X> {

    public Sum(SingularAttribute<P, X> attribute) {
        super(attribute);
    }

    @Override
    public <R> Selection<X> toSelection(CriteriaQuery<R> query, CriteriaBuilder builder, Path<? extends P> path) {
        return builder.sum(path.get(attribute));
    }

}
