/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.opensilex.core.factor.dal;

import java.net.URI;
import java.util.List;
import org.apache.jena.arq.querybuilder.SelectBuilder;
import org.apache.jena.sparql.expr.Expr;
import org.opensilex.sparql.SPARQLQueryHelper;
import org.opensilex.sparql.SPARQLService;
import org.opensilex.sparql.mapping.SPARQLClassObjectMapper;
import org.opensilex.sparql.utils.OrderBy;
import org.opensilex.utils.ListWithPagination;

/**
 *
 * @author vidalmor
 */
public class FactorDAO {

    protected final SPARQLService sparql;

    public FactorDAO(SPARQLService sparql) {
        this.sparql = sparql;
    }

    public FactorModel create(FactorModel instance) throws Exception {
        sparql.create(instance);
        return instance;
    }

    public FactorModel update(FactorModel instance) throws Exception {
        sparql.update(instance);
        return instance;
    }

    public void delete(URI instanceURI) throws Exception {
        sparql.delete(FactorModel.class, instanceURI);
    }

    public FactorModel get(URI instanceURI) throws Exception {
        return sparql.getByURI(FactorModel.class, instanceURI);
    }

    public ListWithPagination<FactorModel> search(String aliasPattern, List<OrderBy> orderByList, Integer page, Integer pageSize) throws Exception {
        SPARQLClassObjectMapper<FactorModel> mapper = SPARQLClassObjectMapper.getForClass(FactorModel.class);

        Expr aliasFilter = SPARQLQueryHelper.regexFilter(FactorModel.ALIAS_FIELD, aliasPattern);
        return sparql.searchWithPagination(
                FactorModel.class,
                (SelectBuilder select) -> {
                    // TODO implements filters
                    if (aliasFilter != null) {
                        select.addFilter(aliasFilter);
                    }
                },
                orderByList,
                page,
                pageSize
        );
    }
}