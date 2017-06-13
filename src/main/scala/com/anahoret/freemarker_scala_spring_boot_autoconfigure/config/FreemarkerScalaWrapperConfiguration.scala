package com.anahoret.freemarker_scala_spring_boot_autoconfigure.config

import javax.annotation.PostConstruct

import com.anahoret.freemarker_scala_spring_boot_autoconfigure.freemarker.wrapper.ScalaWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer

@Configuration
@ConditionalOnProperty(
  name = Array("freemarker.use_scala_wrapper"),
  havingValue = "true")
class FreemarkerScalaWrapperConfiguration extends WebMvcConfigurerAdapter {

  @Autowired
  var freeMarkerConfigurer: FreeMarkerConfigurer = _

  @PostConstruct
  def setScalaObjectWrapper() {
    freeMarkerConfigurer.getConfiguration.setObjectWrapper(new ScalaWrapper())
  }

}
