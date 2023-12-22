package com.shop.admin.controller;

import com.shop.admin.configuration.security.ShopUserDetails;
import com.shop.admin.service.UserService;
import com.shop.admin.util.FileUploadUtil;
import com.shop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {
    @Autowired
    private UserService userService;

    @GetMapping("/account")
    public String viewUserDetails(@AuthenticationPrincipal ShopUserDetails userDetails, Model model) {
        String email = userDetails.getUsername();
        User user = userService.getByEmail(email);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Account");
        return "user/account_form";
    }

    @PostMapping("/account/update")
    public String update(User user, RedirectAttributes redirectAttributes, @AuthenticationPrincipal ShopUserDetails userDetails, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.updateAccount(user);
            String directory = "user-photos/" + savedUser.getId();
            FileUploadUtil.cleanDirectory(directory);
            FileUploadUtil.saveFile(directory, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) user.setPhotos(null);
            userService.updateAccount(user);
        }
        userDetails.setFirstName(user.getFirstName());
        userDetails.setLastName(user.getLastName());
        redirectAttributes.addFlashAttribute("message", "Your account changes have been saved.");
        return "redirect:/account";
    }
}
