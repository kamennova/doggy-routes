package com.kamennova.doggies;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
class MyErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest req, Model model) {
        Object status = req.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            model.addAttribute("code", Integer.valueOf(status.toString()));
        }

        return "error";
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
