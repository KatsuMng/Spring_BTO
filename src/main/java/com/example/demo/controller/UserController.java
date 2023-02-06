package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.model.User;
import com.example.demo.repository.UserMngRepository;
import com.example.demo.util.Authority;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController
{
	@Autowired
	private UserMngRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	private String editHeadline = "";
	private User conUser = new User();

	@GetMapping("/")
	public String mainSite(Authentication loginUser, Model model)
	{
		// System.out.println("メインサイト");
		model.addAttribute("username", loginUser.getName());
		model.addAttribute("authority", loginUser.getAuthorities());
//		if(loginUser.getAuthorities()==Authority.ADMIN) {
//			
//		}
		model.addAttribute("userList", userRepository.findAll());
		return "index";
	}

	@GetMapping("/admin/list")
	public String showAdminList(Model model)
	{
		model.addAttribute("users", userRepository.findAll());
		return "list";
	}

	@GetMapping("/login")
	public String login()
	{
		return "Auth/login";
	}

//	@GetMapping("/")
//	public String getAllUsers(Model model)
//	{
//		model.addAttribute("userList", userRepository.findAll());
//		return "index";
//	}

	@GetMapping("/register")
	public String register(@ModelAttribute User user, Model model)
	{
		editHeadline = "新規登録";
		model.addAttribute("headline", editHeadline);
		conUser.setName(user.getName());
		return "register";
	}

	@GetMapping("/register/{id}")
	public String editUser(@PathVariable Long id, Model model)
	{
		model.addAttribute("user", userRepository.findById(id));
		editHeadline = "ユーザー情報編集";
		model.addAttribute("headline", editHeadline);
		return "register";
	}
	

	@GetMapping("/delete/{id}")
	public String DeleteUser(@PathVariable Long id)
	{
		userRepository.deleteById(id);
		return "redirect:/index";
	}

	@PostMapping("/confirm")
	public String confirm(@Validated @ModelAttribute User user, BindingResult result, Model model)
	{
		conUser = user;
		System.out.println(conUser.getName());

		if (result.hasErrors())
		{
			model.addAttribute("headline", editHeadline);
			return "register";
		}

		conUser.setPassword(passwordEncoder.encode(conUser.getPassword()));
		if (conUser.isAdmin())
		{
			user.setAuthority(Authority.ADMIN);
		} else
		{
			user.setAuthority(Authority.USER);
		}

		return "confirm";
	}

	@PostMapping("/complete")
	public String complete()
	{
		System.out.println(conUser.getName() + " を登録する");
		userRepository.save(conUser);
		System.out.println("データに登録された");
		return "redirect:Auth/login";
	}

	@GetMapping("/index")
	public String getindex(Model model)
	{
		model.addAttribute("userList", userRepository.findAll());
		return "index";
	}

	@PostMapping("/index")
	public String postindex(Model model)
	{
		model.addAttribute("userList", userRepository.findAll());
		return "index";
	}
}
