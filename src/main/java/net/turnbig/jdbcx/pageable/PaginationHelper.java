package net.turnbig.jdbcx.pageable;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import com.google.common.collect.Maps;

public class PaginationHelper {

	/**
	 * 
	 */
	private static final int DEFAULT_PAGE_SIZE = 10;
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
					sort = Sort.by(isDesc ? Direction.DESC : Direction.ASC, field);
				} else {
					sort = sort.and(Sort.by(isDesc ? Direction.DESC : Direction.ASC, field));
				}
			}
		}

		return sort;
	}

	public PageRequest getPagination(String sortby) {
		return getPagination(DEFAULT_PAGE_SIZE, sortby);
	}

	public PageRequest getPagination(Integer pageSize, String sortby) {
		String _sortby = context.getParameter(PAGINATION_ATTRNAME_SORT);
		if (StringUtils.isNotBlank(_sortby)) {
			sortby = _sortby;
		}

		String page = StringUtils.defaultString(context.getParameter(PAGINATION_ATTRNAME_PAGE), "1");
		String size = StringUtils.defaultString(context.getParameter(PAGINATION_ATTRNAME_PAGESIZE),
				String.valueOf(pageSize));
		PageRequest pageRequest = null;
		Sort sort = buildSortBy(sortby);
		if (sort != null) {
			pageRequest = PageRequest.of(Integer.parseInt(page) - 1, Integer.parseInt(size), sort);
		} else {
			pageRequest = PageRequest.of(Integer.parseInt(page) - 1, Integer.parseInt(size));
		}

		return pageRequest;
	}

	public PageRequest getPagination() {
		return getPagination(null);
	}

	public static Map<String, Object> toMap(Page<?> page) {
		HashMap<String, Object> map = Maps.newHashMap();
		map.put("content", page.getContent());
		map.put("totalPages", page.getTotalPages());
		map.put("totalElements", page.getTotalElements());
		map.put("last", page.isLast());
		map.put("first", page.isFirst());
		map.put("pageSize", page.getSize());
		map.put("page", page.getNumber() + 1);
		map.put("numberOfElements", page.getNumberOfElements());
		return map;
	}
}
