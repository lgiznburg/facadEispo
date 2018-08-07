package ru.rsmu.facadeEispo.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.rsmu.facadeEispo.dao.EntrantDao;
import ru.rsmu.facadeEispo.model.Entrant;
import ru.rsmu.facadeEispo.model.EntrantStatus;

import java.util.List;

/**
 * @author leonid.
 */
@Controller
@RequestMapping(value = "/updateErros.htm")
public class UpdateErrors {

    @Autowired
    private EntrantDao entrantDao;

    @RequestMapping()
    public String updateErros() {
        List<Entrant> entrants = entrantDao.findEntrantsWithError();

        for ( Entrant entrant : entrants ) {
            entrant.setStatus( EntrantStatus.UPDATED );
        }

        entrantDao.saveAllEntities( entrants );
        return "redirect:/home.htm";
    }
}
