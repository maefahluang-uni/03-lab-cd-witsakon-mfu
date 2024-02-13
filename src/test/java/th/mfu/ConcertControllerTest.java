package th.mfu;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import th.mfu.domain.Concert;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;



import static org.junit.jupiter.api.Assertions.*;

public class ConcertControllerTest {

    @InjectMocks
    private ConcertController concertController;

    @Mock
    private ConcertRepository concertRepository;

    @Mock
    private Model model;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testListConcerts() {
        List<Concert> concertList = new ArrayList<>();
        // Add sample concerts to the list

        when(concertRepository.findAll()).thenReturn(concertList);

        String viewName = concertController.listConcerts(model);

        assert(viewName.equals("list-concert"));
        verify(concertRepository, times(1)).findAll();
        verify(model, times(1)).addAttribute(eq("concerts"), anyList());
    }

    @Test
    public void testAddAConcertForm() {
        String viewName = concertController.addAConcertForm(model);

        assert(viewName.equals("add-concert-form"));
        verify(model, times(1)).addAttribute(eq("concert"), any(Concert.class));
    }

    @Test
    public void testSaveConcert() {
        Concert newConcert = new Concert();
        when(concertRepository.save(newConcert)).thenReturn(newConcert);

        String viewName = concertController.saveConcert(newConcert);

        assert(viewName.equals("redirect:/concerts"));
        verify(concertRepository, times(1)).save(newConcert);
    }

    @Test
    public void testDeleteConcert() {
        long concertId = 1L;

        String viewName = concertController.deleteConcert(concertId);

        assert(viewName.equals("redirect:/concerts"));
        verify(concertRepository, times(1)).deleteById(concertId);
    }

    @Test
    public void testRemoveAllConcerts() {
        String viewName = concertController.removeAllConcerts();

        assert(viewName.equals("redirect:/concerts"));
        verify(concertRepository, times(1)).deleteAll();
    }
}

