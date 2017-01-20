package com.cashflow.project.frontend.controller.pagination;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;

import static com.cashflow.frontend.support.jsfutil.SelectItemHelper.getSelectItems;
import static java.lang.Integer.max;
import static java.lang.String.format;

/**
 *
 * 
 */
public abstract class PaginationModel {

    @Getter
    @Setter
    private int page = 0;

    @Getter
    @Setter
    private int limit = 5;

    @Getter
    @Setter
    private int count = 0;

    public void nextPage() {
        final int nextOffset = (page + 1) * limit;
        final int maxOffset = count;
        this.page = nextOffset > maxOffset ? page : (page + 1);
        loadData();
    }

    public void previousPage() {
        this.page = max(0, page - 1);
        loadData();
    }

    @Nonnull
    public List<Integer> getLimits() {
        return ImmutableList
                .of(1, 5, 10, 20, 50, 100)
                .stream()
                .filter((l) -> l < count)
                .collect(Collectors.toList());
    }

    @Nonnull
    public SelectItem[] getPages() {
        return getSelectItems(buildPages(), false, "", (i) -> format("%s", i + 1));
    }

    @Nonnull
    public Integer getPageCount() {
        return buildPages().size();
    }

    @Nonnull
    public Integer getMaxPage() {
        return buildPages()
                .stream()
                .sorted((i0, i1) -> i1.compareTo(i0))
                .findFirst()
                .orElse(0);
    }

    @Nonnull
    private List<Integer> buildPages() {
        final ImmutableList.Builder<Integer> pages = ImmutableList.builder();
        int offset = 0;
        int pageIter = 0;
        if (offset + limit > 0) {
            for (; offset < count; offset += limit, pageIter++) {
                pages.add(pageIter);
            }
        }

        if ((count - offset) > 0) {
            pages.add(pageIter);
        }

        return pages.build();
    }

    public abstract void loadData();

}
