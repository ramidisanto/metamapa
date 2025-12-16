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
    private long totalElements;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
}