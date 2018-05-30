package de.dhbw.softwareengineering.digitaljournal.controller;

import de.dhbw.softwareengineering.digitaljournal.business.GoalService;
import de.dhbw.softwareengineering.digitaljournal.business.JournalService;
import de.dhbw.softwareengineering.digitaljournal.business.UserService;
import de.dhbw.softwareengineering.digitaljournal.domain.ContactRequest;
import de.dhbw.softwareengineering.digitaljournal.domain.User;
import de.dhbw.softwareengineering.digitaljournal.util.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    private final UserService userService;
    private final JournalService journalService;
    private final GoalService goalService;


    public ProfileController(UserService userService, JournalService journalService, GoalService goalService) {
        this.userService = userService;
        this.journalService = journalService;
        this.goalService = goalService;
    }

    @GetMapping("/profile")
    public String showProfile(Model model, Principal principal){
        User user = userService.findByName(principal.getName());

        model.addAttribute(Constants.SESSION_CONTACTREQUEST,new ContactRequest());
        model.addAttribute("user", user);
        model.addAttribute("journalCount", journalService.countByUsername(user.getUsername()));
        model.addAttribute("activeGoals", goalService.getActiveGoals(user.getUsername()));

        return "profile";
    }
}
