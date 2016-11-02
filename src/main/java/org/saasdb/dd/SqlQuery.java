package org.saasdb.dd;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

public class SqlQuery {
	private boolean distinct;
	private String productName;

	private final ClauseList select;
	private final ClauseList from;
	private final ClauseList where;
	private final ClauseList groupBy;
	private final ClauseList having;
	private final ClauseList orderBy;

	private final List fromAliases;

	private final StringBuffer buf;


	public SqlQuery()  {
		this.productName = "postgres";

		this.select = new ClauseList(true);
		this.from = new ClauseList(true);

		this.where = new ClauseList(false);
		this.groupBy = new ClauseList(false);
		this.having = new ClauseList(false);
		this.orderBy = new ClauseList(false);
		this.fromAliases = new ArrayList();
		this.buf = new StringBuffer(128);
	}

	public void setDistinct(final boolean distinct) {
		this.distinct = distinct;
	}

	private String initializeQuoteIdentifierString(final DatabaseMetaData databaseMetaData)  {
		String s = null;
		try {
			s = databaseMetaData.getIdentifierQuoteString();
		} catch (SQLException e) {
			// throw new Exception("while quoting identifier", e);

			return null;
		}

		if (s.trim().length() == 0)
			s = null;

		return s;
	}


	
	public boolean addFromTable(final String schema, final String table, final String alias)  {
		if (fromAliases.contains(alias)) {
			return false;
		}

		buf.setLength(0);

		if (schema == null) {

				buf.append(table);
		} else {
				buf.append(schema);

			buf.append('.');

				buf.append(table);
		}

		if (alias != null) {
			if (allowsAs()) {
				buf.append(" as ");
			} else {
				buf.append(' ');
			}

			buf.append(alias);
			fromAliases.add(alias);
		}

		from.add(buf.toString());

		return true;
	}

	private boolean allowsAs() {
		return true;
	}


	public boolean addFromQuery(final String query, final String alias)  {

		if (fromAliases.contains(alias)) {
			return false;
		}

		buf.setLength(0);

		buf.append('(');
		buf.append(query);
		buf.append(')');
		if (alias != null) {

			if (allowsAs()) {
				buf.append(" as ");
			} else {
				buf.append(' ');
			}
			// quoteIdentifier(alias, buf);
			buf.append(alias);
			fromAliases.add(alias);
		}

		from.add(buf.toString());
		return true;
	}

	public void addFrom(final SqlQuery sqlQuery, final String alias)  {
		addFromQuery(sqlQuery.toString(), alias);
	}


	public void addSelect(final String expression)  {
		addSelect(expression, "c" + select.size());
	}

	public void addSelect(final String expression, final String alias) {
		buf.setLength(0);

		buf.append(expression);
		if (alias != null) {
			buf.append(" as ");
			buf.append(alias);
		}

		select.add(buf.toString());
	}

	public void addWhere(final String exprLeft, final String exprMid, final String exprRight) {
		int len = exprLeft.length() + exprMid.length() + exprRight.length();
		StringBuffer buf = new StringBuffer(len);

		buf.append(exprLeft);
		buf.append(exprMid);
		buf.append(exprRight);

		addWhere(buf.toString());
	}

	public void addWhere(final String expression) {
		where.add(expression);
	}

	public void addGroupBy(final String expression) {
		groupBy.add(expression);
	}

	public void addHaving(final String expression) {
		having.add(expression);
	}

	public void addOrderBy(final String expression) {
		orderBy.add(expression);
	}

	public String toString() {
		buf.setLength(0);

		select.toBuffer(buf, distinct ? "select distinct " : "select ", ", ");
		from.toBuffer(buf, " from ", ", ");
		where.toBuffer(buf, " where ", " and ");
		groupBy.toBuffer(buf, " group by ", ", ");
		having.toBuffer(buf, " having ", " and ");
		orderBy.toBuffer(buf, " order by ", ", ");

		return buf.toString();
	}

	private class ClauseList extends ArrayList {
		private final boolean allowDups;

		ClauseList(final boolean allowDups) {
			this.allowDups = allowDups;
		}

		void add(final String element) {
			if (allowDups || !contains(element)) {
				super.add(element);
			}
		}

		void toBuffer(final StringBuffer buf, final String first, final String sep) {
			Iterator it = iterator();
			boolean firstTime = true;
			while (it.hasNext()) {
				String s = (String) it.next();

				if (firstTime) {
					buf.append(first);
					firstTime = false;
				} else {
					buf.append(sep);
				}

				buf.append(s);
			}
		}
	}
}