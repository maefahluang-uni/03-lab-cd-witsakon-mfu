package th.mfu;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import th.mfu.domain.Concert;

@Controller
public class ConcertController {
    // TODO: define repository for concert and @Autowired
    @Autowired
    ConcertRepository repository;

    
    public ConcertController(ConcertRepository repository) {
        this.repository = repository;
    }

    //TODO: add initBinder for date format
    @InitBinder
    public final void initBinderUsuariosFormValidator(final WebDataBinder binder, final Locale locale) {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", locale);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    @GetMapping("/concerts")
    public String listConcerts(Model model) {
        // TODO: list all concerts
        model.addAttribute("concerts", repository.findAll());
        // TODO: return a template to list concerts
        return "list-concert";
    }

   

    @GetMapping("/add-concert")
    public String addAConcertForm(Model model) {
        // TODO: pass blank concert to a form
        model.addAttribute("concert", new Concert());
        // TODO: return a template for concert form
        return "add-concert-form";
    }

    @PostMapping("/concerts")
    public String saveConcert(@ModelAttribute Concert concert) {
        // TODO: add concert to DB
        repository.save(concert);
        // TODO: redirect to list concerts
        return "redirect:/concerts";
    }

    @GetMapping("/concerts/{id}")
    public String getConcert(Model model, @PathVariable Long id) {
        //TODO: find concert by id
        Concert concert = repository.findById(id).get();
        // TODO: list all concerts
        model.addAttribute("concert", concert);
        // TODO: return a template for concert form
        return "add-concert-form";
    }


    @GetMapping("/delete-concert/{id}")
    public String deleteConcert(@PathVariable long id) {
        // TODO: delete concert from DB
        repository.deleteById(id);
        // TODO: redirect to list concerts
        return "redirect:/concerts";
    }

    
    @GetMapping("/delete-concert")
    public String removeAllConcerts() {
        // delete all employees 
        repository.deleteAll();
        return "redirect:/concerts";
    }

}
