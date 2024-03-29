package com.shop.admin.controller;

import java.io.IOException;
import java.util.List;

import com.shop.admin.exporter.Exporter;
import com.shop.admin.exporter.UserExporter;
import com.shop.admin.util.FileUploadUtil;
import com.shop.admin.exception.UserNotFoundException;
import com.shop.admin.service.UserService;
import com.shop.common.entity.Role;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;

import com.shop.common.entity.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public String getFirstPage(Model model) {
        return paginate(1, "firstName", "asc", null, model);
    }

    @GetMapping("/users/page/{pageNumber}")
    public String paginate(@PathVariable(name = "pageNumber") int pageNumber,
                           @Param("sortField") String sortField,
                           @Param("sortOrder") String sortOrder,
                           @Param("keyword") String keyword,
                           Model model) {
        Page<User> userPage = userService.paginate(pageNumber, sortField, sortOrder, keyword);
        List<User> users = userPage.getContent();
        long pageSize = userPage.getSize();
        long totalPages = userPage.getTotalPages();
        long startCount = (pageNumber - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        long totalCount = userPage.getTotalElements();
        if (endCount > totalCount) endCount = totalCount;
        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";
        model.addAttribute("users", users);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("keyword", keyword);
        return "user/users";
    }

    @GetMapping("/users/create")
    public String create(Model model) {
        User user = new User();
        List<Role> roles = userService.getRoles();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("pageTitle", "Create User");
        return "user/user_form";
    }

    @GetMapping("/users/update/{id}")
    public String update(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = userService.getById(id);
            List<Role> roles = userService.getRoles();
            model.addAttribute("user", user);
            model.addAttribute("roles", roles);
            model.addAttribute("pageTitle", "Edit User " + user.getFirstName());
            return "user/user_form";
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/users";
        }
    }

    @PostMapping("/users/save")
    public String save(User user, RedirectAttributes redirectAttributes, @RequestParam("fileImage") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhoto(fileName);
            User savedUser = userService.save(user);
            String directory = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDirectory(directory);
            FileUploadUtil.saveFile(directory, fileName, multipartFile);
        } else {
            if (user.getPhoto().isEmpty()) user.setPhoto(null);
            userService.save(user);
        }
        redirectAttributes.addFlashAttribute("message", "The user has been saved.");
        return redirect(user);
    }

    @GetMapping("/users/{id}/enabled/{enabled}")
    public String updateEnabled(@PathVariable(name = "id") Integer id, @PathVariable(name = "enabled") boolean enabled, RedirectAttributes redirectAttributes) {
        userService.updateStatus(id, enabled);
        String message = "The user id " + id + " has been " + (enabled ? "enabled." : "disabled.");
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The user id " + id + " has been deleted successfully");
        } catch (UserNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
        }
        return "redirect:/users";
    }

    @GetMapping("/users/export/csv")
    public void exportCSV(HttpServletResponse response) throws IOException {
        List<User> users = userService.getAll();
        Exporter exporter = new UserExporter();
        exporter.exportCSV(users, response);
    }

    @GetMapping("/users/export/excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<User> users = userService.getAll();
        Exporter exporter = new UserExporter();
        exporter.exportExcel(users, response);
    }

    @GetMapping("/users/export/pdf")
    public void exportPDF(HttpServletResponse response) throws IOException {
        List<User> users = userService.getAll();
        Exporter exporter = new UserExporter();
        exporter.exportPDF(users, response);
    }

    public String redirect(User user) {
        String email = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortField=id&sortOrder=asc&keyword=" + email;
    }
}
