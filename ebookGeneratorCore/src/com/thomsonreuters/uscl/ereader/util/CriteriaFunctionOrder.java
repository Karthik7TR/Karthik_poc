package com.thomsonreuters.uscl.ereader.util;

import java.util.Objects;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Order;

public final class CriteriaFunctionOrder extends Order {
    private static final long serialVersionUID = 299962908320067221L;

    private final SqlFunctionCall sqlFunctionCall;

    private CriteriaFunctionOrder(final String propertyName, final boolean ascending,
                                  final SqlFunctionCall sqlFunctionCall) {
        super(propertyName, ascending);
        this.sqlFunctionCall = sqlFunctionCall;
    }

    @Override
    public String toSqlString(final Criteria criteria, final CriteriaQuery criteriaQuery) {
        final String orderByClouse = super.toSqlString(criteria, criteriaQuery);
        final String[] orderByColumns = criteriaQuery.getColumnsUsingProjection(criteria, getPropertyName());
        final String[] orderByColumnsWithFunction = Stream.of(orderByColumns)
            .map(sqlFunctionCall::wrapPropertyWithFunction)
            .toArray(String[]::new);
        return StringUtils.replaceEach(orderByClouse, orderByColumns, orderByColumnsWithFunction);
    }

    public static CriteriaFunctionOrder asc(final String propertyName, final SqlFunctionCall sqlFunctionCall) {
        Objects.requireNonNull(sqlFunctionCall, "sqlFunction cannot be null");
        return new CriteriaFunctionOrder(propertyName, true, sqlFunctionCall);
    }

    public static CriteriaFunctionOrder desc(final String propertyName, final SqlFunctionCall sqlFunctionCall) {
        Objects.requireNonNull(sqlFunctionCall, "sqlFunction cannot be null");
        return new CriteriaFunctionOrder(propertyName, false, sqlFunctionCall);
    }

    public interface SqlFunctionCall {
        String wrapPropertyWithFunction(String propertyName);

        default SqlFunctionCall includeFunction(final SqlFunctionCall functionToInclude) {
            return propertyName -> wrapPropertyWithFunction(functionToInclude.wrapPropertyWithFunction(propertyName));
        }

        static SqlFunctionCall trim() {
            return propertyName -> String.format("TRIM(%s)", propertyName);
        }

        static SqlFunctionCall toNumber() {
            return propertyName -> String.format("TO_NUMBER(%s)", propertyName);
        }
    }

    public interface SqlFunctionWithArgsCall extends SqlFunctionCall {
        static SqlFunctionCall regexpSubstr(final String regexpr, final int startIndex, final int occurence) {
            return propertyName -> String.format("REGEXP_SUBSTR(%s, '%s', %s, %s)", propertyName, regexpr, startIndex, occurence);
        }
    }
}
