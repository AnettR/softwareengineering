package de.dhbw.softwareengineering.digitaljournal.controller;

import de.dhbw.softwareengineering.digitaljournal.business.FriendService;
import de.dhbw.softwareengineering.digitaljournal.business.GoalService;
import de.dhbw.softwareengineering.digitaljournal.business.JournalService;
import de.dhbw.softwareengineering.digitaljournal.business.SharedJournalService;
import de.dhbw.softwareengineering.digitaljournal.domain.*;
import de.dhbw.softwareengineering.digitaljournal.util.Constants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static de.dhbw.softwareengineering.digitaljournal.util.Constants.*;

@Controller
@RequestMapping("/journal")
public class JournalController {

    private final JournalService journalService;
    private final GoalService goalService;
    private final FriendService friendService;
    private final SharedJournalService sharedJournalService;


    public JournalController(JournalService journalService,
                             GoalService goalService,
                             FriendService friendService,
                             SharedJournalService sharedJournalService) {
        this.journalService = journalService;
        this.goalService = goalService;
        this.friendService = friendService;
        this.sharedJournalService = sharedJournalService;
    }

    @GetMapping
    public String showFeed(Model model, Principal principal) {
        List<Journal> journals = journalService.findAll(principal.getName());
        List<Goal> goals = goalService.findLatestsGoals(principal.getName());
        List<Friend> friends = friendService.findAll(principal.getName());
        addSharedJournals(principal, journals);
        journals = sortJournals(journals);
        List<Friend> approvedFriends = friendService.getAllApproved(principal.getName());
        model.addAttribute("shareFriends", approvedFriends);

        model.addAttribute("friends", friends);
        if (!model.containsAttribute(Constants.SHOW_FURTHER_GOALS_BTN))
            model.addAttribute(Constants.SHOW_FURTHER_GOALS_BTN, true);
        model.addAttribute(Constants.SESSION_CONTACTREQUEST, new ContactRequest());
        if (!model.containsAttribute(Constants.SESSION_GOALS))
            model.addAttribute(Constants.SESSION_GOALS, goals);
        model.addAttribute(Constants.SESSION_JOURNAL, new Journal());
        if (!model.containsAttribute(Constants.SESSION_JOURNALS))
            model.addAttribute(Constants.SESSION_JOURNALS, journals);
        model.addAttribute(Constants.SESSION_GOAL, new Goal());
        model.addAttribute("nofifyGoalExceeded", goalService.hasNotnotifiedGoals(principal.getName()));
        return "feed";
    }

    private List<Journal> sortJournals(List<Journal> journals) {
        journals = journals.stream().sorted(Comparator.comparingLong(Journal::getDate)).collect(Collectors.toList());
        Collections.reverse(journals);
        return journals;
    }

    private void addSharedJournals(Principal principal, List<Journal> journals) {
        List<SharedJournal> sharedJournals = sharedJournalService.findAllSharedJournals(principal.getName());
        for(SharedJournal sharedJournal : sharedJournals) {
            Journal journal = journalService.findById(sharedJournal.getJournalName());
            journal.setShared(true);
            journals.add(journal);
        }
    }

    @PostMapping("/create")
    public String createJournal(@Valid @ModelAttribute(Constants.SESSION_JOURNAL) Journal journal,
                                BindingResult result,
                                Model model,
                                Principal principal) {
        model.addAttribute(Constants.SESSION_CONTACTREQUEST, new ContactRequest());

        if (result.hasErrors())
            return "error";

        if (journal.getJournalName().equals("")) {
            model.addAttribute(STATUS_ATTRIBUTE_NAME, STATUSCODE_EMPTYFORM);
        } else if (journal.getJournalName().length() > 100) {
            model.addAttribute(STATUS_ATTRIBUTE_NAME, STATUSCODE_MODAL_TEMP);
            model.addAttribute(STATUSCODE_MODAL_HEADER, "Journalname to long!");
            model.addAttribute(STATUSCODE_MODAL_BODY, "Please enter an shorter Journalname.");
        } else if (journal.getContent().length() > JOURNAL_CONTENT_SIZE) {
            model.addAttribute(STATUS_ATTRIBUTE_NAME, STATUSCODE_MODAL_TEMP);
            model.addAttribute(STATUSCODE_MODAL_HEADER, "Content to long!");
            model.addAttribute(STATUSCODE_MODAL_BODY, "Please make your content shorter.");
        } else {
            journal.setUsername(principal.getName());
            journal.setDate(System.currentTimeMillis());
            journalService.save(journal);
        }

        return Constants.REDIRECT_JOURNAl;
    }


    @GetMapping("/edit/{journalId}")
    public String showEditJournal(@PathVariable String journalId, Model model, Principal principal, HttpSession session) {
        model.addAttribute(Constants.SESSION_CONTACTREQUEST, new ContactRequest());

        Journal journal = journalService.findById(journalId);

        if (journal.getUsername().equals(principal.getName())) {
            model.addAttribute(Constants.SESSION_JOURNAL, journal);
            session.setAttribute(Constants.SESSION_CURRENTJOURNAL, journal);
            return "editjournal";
        }

        return Constants.REDIRECT_JOURNAl;
    }

    @PostMapping("/edit")
    public String editJournal(@Valid @ModelAttribute(Constants.SESSION_JOURNAL) final Journal editedJournal,
                              final BindingResult result,
                              Model model,
                              Principal principal,
                              HttpSession session) {
        model.addAttribute(Constants.SESSION_CONTACTREQUEST, new ContactRequest());

        if (result.hasErrors())
            return "error";

        if (editedJournal.getJournalName().equals("")) {
            model.addAttribute(STATUS_ATTRIBUTE_NAME, STATUSCODE_EMPTYFORM);
        } else if (editedJournal.getJournalName().length() > 100) {
            model.addAttribute(STATUS_ATTRIBUTE_NAME, STATUSCODE_MODAL_TEMP);
            model.addAttribute(STATUSCODE_MODAL_HEADER, "Journalname too long!");
            model.addAttribute(STATUSCODE_MODAL_BODY, "Please enter an shorter Journalname.");
        } else if (editedJournal.getContent().length() > JOURNAL_CONTENT_SIZE) {
            model.addAttribute(STATUS_ATTRIBUTE_NAME, STATUSCODE_MODAL_TEMP);
            model.addAttribute(STATUSCODE_MODAL_HEADER, "Content too long!");
            model.addAttribute(STATUSCODE_MODAL_BODY, "Please make your content shorter.");
        } else if (session.getAttribute(Constants.SESSION_CURRENTJOURNAL) != null) {
            Journal oldJournal = (Journal) session.getAttribute(Constants.SESSION_CURRENTJOURNAL);

            if (oldJournal.getUsername().equals(principal.getName())) {
                editedJournal.setJournalid(oldJournal.getJournalid());
                editedJournal.setUsername(oldJournal.getUsername());
                editedJournal.setDate(System.currentTimeMillis());
                journalService.update(editedJournal);
            }
        }

        return Constants.REDIRECT_JOURNAl;
    }

    @GetMapping("/delete")
    public String delete(Model model, HttpSession session) {
        Journal journal = (Journal) session.getAttribute(Constants.SESSION_CURRENTJOURNAL);
        model.addAttribute("delete", "true");
        model.addAttribute(Constants.SESSION_JOURNAL, journal);
        model.addAttribute(Constants.SESSION_CONTACTREQUEST, new ContactRequest());

        return "editjournal";
    }

    @PostMapping("/delete")
    public String deleteConfirm(Model model, HttpSession session) {
        model.addAttribute(Constants.SESSION_CONTACTREQUEST, new ContactRequest());

        Journal oldJournal = (Journal) session.getAttribute(Constants.SESSION_CURRENTJOURNAL);
        journalService.deleteById(oldJournal.getJournalid());
        session.removeAttribute(Constants.SESSION_CURRENTJOURNAL);

        return Constants.REDIRECT_JOURNAl;
    }

    @GetMapping("/share/{journalId}")
    public String showShareModal(@PathVariable String journalId, HttpSession session, RedirectAttributes redir) {
        Journal journal = journalService.findById(journalId);
        if(journal == null)
            return Constants.REDIRECT_JOURNAl;

        session.setAttribute("shareJournalID", journalId);
        redir.addFlashAttribute(STATUS_ATTRIBUTE_NAME, "shareJournal");
        return Constants.REDIRECT_JOURNAl;
    }

    @GetMapping("/share/add/{CoAuthor}")
    public String addCoAuthor(@PathVariable("CoAuthor") String coAuthor, HttpSession session, RedirectAttributes redir) {
        String  journalID = (String) session.getAttribute("shareJournalID");
        SharedJournal sharedJournal = new SharedJournal(journalID, coAuthor);
        sharedJournalService.save(sharedJournal);
        return Constants.REDIRECT_JOURNAl;
    }

}
