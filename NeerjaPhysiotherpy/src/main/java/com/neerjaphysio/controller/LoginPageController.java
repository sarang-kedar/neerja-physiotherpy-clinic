package com.neerjaphysio.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Redirects to login/index pages within the application context.
 * Note: The application is already deployed at /neerjaPhysiotherpyClinic
 * so we use relative paths to avoid context path doubling.
 */
@Controller
public class LoginPageController {

    // Not needed - WebConfig handles these mappings
    // This controller is kept minimal for any additional routes
}
