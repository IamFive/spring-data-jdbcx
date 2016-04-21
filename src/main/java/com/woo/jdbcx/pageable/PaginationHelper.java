package com.woo.jdbcx.pageable;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class PaginationHelper {

	public static final String PAGINATION_ATTRNAME_PAGE = "page";
	public static final String PAGINATION_ATTRNAME_PAGESIZE = "pagesize";
	public static final String PAGINATION_ATTRNAME_SORT = "sortby";

	@Autowired
	private HttpServletRequest context;

	/**
	 * @param sortby
	 * @return
	 */
	private Sort buildSortBy(String sortby) {
		Sort sort = null;
		if (StringUtils.isNotBlank(sortby)) {
			String[] split = StringUtils.split(sortby, ",");
			for (String s : split) {
				boolean isDesc = StringUtils.startsWith(s, "-");
				String field = isDesc ? StringUtils.removeStart(s, "-") : s;
				if (sort == null) {
					sort = new Sort(isDesc ? Direction.DESC : Direction.ASC, field);
				} else {
					sort = sort.and(new Sort(isDesc ? Direction.DESC : Direction.ASC, field));
				}
			}
		}

		return sort;
	}

	public PageRequest getPagination(String sortby) {

		String page = StringUtils.defaultString(context.getParameter(PAGINATION_ATTRNAME_PAGE), "1");
		String pageSize = StringUtils.defaultString(context.getParameter(PAGINATION_ATTRNAME_PAGESIZE), "10");

		PageRequest pageRequest = null;
		Sort sort = buildSortBy(sortby);
		if (sort != null) {
			pageRequest = new PageRequest(Integer.parseInt(page) - 1, Integer.parseInt(pageSize), sort);
		} else {
			pageRequest = new PageRequest(Integer.parseInt(page) - 1, Integer.parseInt(pageSize));
		}

		return pageRequest;
	}

	public PageRequest getPagination() {
		String sortby = context.getParameter(PAGINATION_ATTRNAME_SORT);
		return getPagination(sortby);
	}

}
