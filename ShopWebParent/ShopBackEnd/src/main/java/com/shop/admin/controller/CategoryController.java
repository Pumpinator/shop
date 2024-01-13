package com.shop.admin.controller;

import com.shop.admin.exception.CategoryNotFoundException;
import com.shop.admin.service.CategoryService;
import com.shop.admin.util.FileUploadUtil;
import com.shop.common.entity.Category;
import com.shop.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public String getFirstPage(Model model) {
        return paginate(1, "name", "asc", null, model);
    }

    @GetMapping("/categories/page/{pageNumber}")
    public String paginate(@PathVariable(name = "pageNumber") int pageNumber,
                           @Param("sortField") String sortField,
                           @Param("sortOrder") String sortOrder,
                           @Param("keyword") String keyword,
                           Model model) {
        Page<Category> categoryPage = categoryService.paginate(pageNumber, sortField, sortOrder, keyword);
        List<Category> categories = categoryPage.getContent();
        long pageSize = categoryPage.getSize();
        long totalPages = categoryPage.getTotalPages();
        long startCount = (pageNumber - 1) * pageSize + 1;
        long endCount = startCount + pageSize - 1;
        long totalCount = categoryPage.getTotalElements();
        if (endCount > totalCount) endCount = totalCount;
        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";
        model.addAttribute("categories", categories);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("currentPage", pageNumber);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("reverseSortOrder", reverseSortOrder);
        model.addAttribute("keyword", keyword);
        return "category/categories";
    }

    @GetMapping("/categories/create")
    public String create(Model model) {
        Category category = new Category();
        category.setEnabled(true);
        List<Category> categories = categoryService.getAll();
        model.addAttribute("category", category);
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Create Category");
        return "category/category_form";
    }

    @GetMapping("/categories/update/{id}")
    public String update(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Category category = categoryService.getById(id);
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Edit Category " + category.getName());
            return "category/category_form";
        } catch (CategoryNotFoundException exception) {
            redirectAttributes.addFlashAttribute("message", exception.getMessage());
            return "redirect:/categories";
        }
    }

    @PostMapping("/categories/save")
    public String save(Category category, RedirectAttributes redirectAttributes, @RequestParam("image")MultipartFile multipartFile) throws IOException {
        if(!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);
            Category savedCategory = categoryService.save(category);
            String directory = "category-photos/" + savedCategory.getId();
            FileUploadUtil.cleanDirectory(directory);
            FileUploadUtil.saveFile(directory, fileName, multipartFile);
        } else {
            if(category.getImage().isEmpty()) category.setImage(null);
            categoryService.save(category);
        }
        redirectAttributes.addFlashAttribute("message", "The category has been saved.");
        return redirect(category);
    }

    public String redirect(Category category) {
        String name = category.getName();
        return "redirect:/categories/page/1?sortField=id&sortOrder=asc&keyword=" + name;
    }
}
