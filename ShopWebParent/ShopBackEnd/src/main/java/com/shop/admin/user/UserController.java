package com.shop.admin.user;

import java.util.List;

import com.shop.common.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.shop.common.entity.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getAll(Model model) {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/users/create")
    public String create(Model model) {
        User user = new User();
        List<Role> roles = userService.getRoles();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Create User");
        return "user_form";
    }

    @GetMapping("/users/update/{id}")
    public String update(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getById(id);
            List<Role> roles = userService.getRoles();
            model.addAttribute("user", user);
            model.addAttribute("roles", roles);
            model.addAttribute("pageTitle", "Edit User " + user.getFirstName());
            return "user_form";
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/users";
        }
    }

    @PostMapping("/users/save")
    public String save(User user, RedirectAttributes redirectAttributes) {
        System.out.println(user); // ELIMINAR PRONTO
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "The user has been saved.");
        return "redirect:/users";
    }
}
