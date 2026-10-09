package com.ashlesh.mycoolproject.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

// Common page/size/sort validation so we do not repeat it in every service
public class PaginationUtil {

    private static final int MAX_PAGE_SIZE = 50;

    public static Pageable createPageable(int page, int size, String sortBy,
                                          String direction, List<String> allowedSortFields) {
        if (page < 0) {
            throw new IllegalArgumentException("Page number must be 0 or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("Page size must be between 1 and " + MAX_PAGE_SIZE);
        }
        if (!allowedSortFields.contains(sortBy)) {
            throw new IllegalArgumentException("Invalid sortBy '" + sortBy + "'. Allowed fields: " + allowedSortFields);
        }

        Sort sort;
        if (direction.equalsIgnoreCase("asc")) {
            sort = Sort.by(sortBy).ascending();
        } else if (direction.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            throw new IllegalArgumentException("Direction must be asc or desc");
        }
        return PageRequest.of(page, size, sort);
    }
}