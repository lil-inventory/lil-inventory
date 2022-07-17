package org.ivcode.inventory.controller

import org.ivcode.inventory.service.GroupNavigationService
import org.ivcode.inventory.service.model.NavigationElement
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/navigation")
class NavigationController(
    val groupNavigationService: GroupNavigationService
) {

    @GetMapping("/")
    fun getRoot(): NavigationElement {
        return groupNavigationService.getRootGroupNavigation()
    }

    @GetMapping("/{groupId}")
    fun getGroupNavigation(@PathVariable groupId: Long): NavigationElement =
        groupNavigationService.getGroupNavigation(groupId)

}
