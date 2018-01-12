package me.shenchao.webhunger.control.util;

import java.util.List;
import java.util.Map;

/**
 * datatable serverside model
 * java unfriendly ajax data parameter
 */
public class DataTableCriterias {

    private int draw;
    private int start;
    private int length;

    private Map<SearchCriterias, String> search;

    private List<Map<ColumnCriterias, String>> columns;

    private List<Map<OrderCriterias, String>> order;

    private String dataTableType;

    public int getDraw() {
        return draw;
    }

    public void setDraw(int draw) {
        this.draw = draw;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public Map<SearchCriterias, String> getSearch() {
        return search;
    }

    public void setSearch(Map<SearchCriterias, String> search) {
        this.search = search;
    }

    public List<Map<ColumnCriterias, String>> getColumns() {
        return columns;
    }

    public void setColumns(List<Map<ColumnCriterias, String>> columns) {
        this.columns = columns;
    }

    public List<Map<OrderCriterias, String>> getOrder() {
        return order;
    }

    public void setOrder(List<Map<OrderCriterias, String>> order) {
        this.order = order;
    }

    public String getDataTableType() {
        return dataTableType;
    }

    public void setDataTableType(String dataTableType) {
        this.dataTableType = dataTableType;
    }

    public enum SearchCriterias {
        value,
        regex
    }

    public enum OrderCriterias {
        column,
        dir
    }

    public enum ColumnCriterias {
        data,
        name,
        searchable,
        orderable,
        searchValue,
        searchRegex
    }

}