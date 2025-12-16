package Modelos.Entidades;

import Modelos.Entidades.DTOs.HechoDTOoutput;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PagedHechoResponse {
    private List<HechoDTOoutput> content;
    private int currentPage;
    private int totalPages;
    private int totalElements;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public PagedHechoResponse(List<HechoDTOoutput> content, int currentPage, int totalPages, int totalElements, int pageSize, boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }
}