package com.codeup.codeupspringblog.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@Controller
public class RollDiceController {

    @GetMapping("/roll-dice")
    public String rollDice(Model model) {
        model.addAttribute("pageTitle", "Roll Dice");
        return "roll-dice";
    }

    @GetMapping("/roll-dice/{n}")
    public String guess(@PathVariable int n, Model model) {
        model.addAttribute("pageTitle", "Guess");

        ArrayList<Integer> rolls = new ArrayList<>();
        int correctGuess = 0;
        int rand;

        for (int i = 0; i < 10; i++) {
            rand = ThreadLocalRandom.current().nextInt(1, 7);
            System.out.println("rand = " + rand);
            if (n == rand) {
                correctGuess++;
            }
            rolls.add(rand);
        }

        model.addAttribute("n", n);
        model.addAttribute("correct", correctGuess);
        model.addAttribute("rolls", rolls);

        return "guess";
    }
}
