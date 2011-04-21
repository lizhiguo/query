package com.ctp.cdi.query.builder;

import javax.persistence.EntityManager;

import com.ctp.cdi.query.Query;
import com.ctp.cdi.query.handler.QueryInvocationContext;
import com.ctp.cdi.query.meta.MethodType;
import com.ctp.cdi.query.meta.QueryInvocation;
import com.ctp.cdi.query.param.Parameters;
import com.ctp.cdi.query.util.QueryUtils;

/**
 * Create the query based on method annotations.
 * @author thomashug
 */
@QueryInvocation(MethodType.ANNOTATED)
public class AnnotatedQueryBuilder extends QueryBuilder {
    
    @Override
    public Object execute(QueryInvocationContext queryContext) {
        Query query = queryContext.getMethod().getAnnotation(Query.class);
        javax.persistence.Query jpaQuery = createJpaQuery(query, queryContext);
        if (returnsList(queryContext)) {
            return jpaQuery.getResultList();
        } else {
            return jpaQuery.getSingleResult();
        }
    }
    
    private javax.persistence.Query createJpaQuery(Query query, QueryInvocationContext context) {
        EntityManager entityManager = context.getEntityManager();
        Parameters params = context.getParams();
        javax.persistence.Query result = null;
        if (QueryUtils.isNotEmpty(query.named())) {
            result = params.applyTo(entityManager.createNamedQuery(query.named()));
        } else {
            result = params.applyTo(entityManager.createQuery(query.value()));
        }
        return applyRestrictions(context, result);
    }

}
