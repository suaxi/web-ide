package com.sw.controller;

import com.sw.service.ExecuteStringSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Wang Hao
 * @date 2023/7/2 16:10
 * @description
 */
@Controller
public class RunCodeController {

    @Autowired
    private ExecuteStringSourceService executeStringSourceService;

    private static final String defaultSource =
            "import java.util.Scanner;\n" +
            "\n" +
            "public class Run {\n" +
            "    public static void main(String[] args) {\n" +
            "        Scanner in = new Scanner(System.in);\n" +
            "        System.out.println(in.next());\n" +
            "    }\n" +
            "}";

    @GetMapping
    public String test(Model model) {
        model.addAttribute("lastSource", defaultSource);
        return "ide";
    }

    @PostMapping("/run")
    public String run(@RequestParam("source") String source, @RequestParam("systemIn") String systemIn, Model model) {
        String result = executeStringSourceService.execute(source, systemIn);
        result = result.replaceAll(System.lineSeparator(), "<br/>");
        model.addAttribute("lastSource", source);
        model.addAttribute("lastSystemIn", systemIn);
        model.addAttribute("result", result);
        return "ide";
    }
}
