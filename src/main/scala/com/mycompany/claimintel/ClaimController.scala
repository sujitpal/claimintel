package com.mycompany.claimintel

import org.springframework.stereotype.Controller
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class ClaimController @Autowired() (nameService: Name){

  @RequestMapping(value = Array("/index.html"), 
      method = Array(RequestMethod.GET))
  def index(model: Model): String = {
    model.addAttribute("name", nameService.name)
    "index"
  }
}