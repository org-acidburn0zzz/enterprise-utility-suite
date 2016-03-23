/*******************************************************************************
 * Copyright (C) 2016 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version 2 only
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *******************************************************************************/

/**
 *
 */
package com.blackducksoftware.tools.highsev;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.sdk.codecenter.attribute.data.AbstractAttribute;
import com.blackducksoftware.sdk.codecenter.attribute.data.AttributeNameToken;
import com.blackducksoftware.sdk.codecenter.cola.data.Component;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentPageFilter;
import com.blackducksoftware.sdk.codecenter.cola.data.ComponentUpdate;
import com.blackducksoftware.sdk.codecenter.common.data.AttributeValue;
import com.blackducksoftware.sdk.codecenter.fault.SdkFault;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilityPageFilter;
import com.blackducksoftware.sdk.codecenter.vulnerability.data.VulnerabilitySummary;
import com.blackducksoftware.tools.connector.codecenter.CodeCenterServerWrapper;

/**
 * For each catalog component, identify the highest severity level of any
 * vulnerability by writing that level to a component custom attribute.
 *
 * @author Ari Kamen
 * @date Jul 22, 2014
 *
 */
public class HighSevProcessor {
    private final Logger log = LoggerFactory.getLogger(this.getClass()
            .getName());

    private final HighSevConfigManager config;

    private final CodeCenterServerWrapper ccWrapper;

    // Map to assist sorting
    private static final HashMap<String, Integer> VULNERABILITY_SEVERITY_MAP = new HashMap<String, Integer>();

    private final List<String> failedComponents = new ArrayList<String>();

    private Integer totalComponents = 0;

    public HighSevProcessor(File file) throws Exception {
        try {

            config = new HighSevConfigManager(file);
            ccWrapper = new CodeCenterServerWrapper(config);

            VULNERABILITY_SEVERITY_MAP.put("low",
                    HighSevConstants.SEV_VALUE_LOW);
            VULNERABILITY_SEVERITY_MAP.put("medium",
                    HighSevConstants.SEV_VALUE_MED);
            VULNERABILITY_SEVERITY_MAP.put("high",
                    HighSevConstants.SEV_VALUE_HIGH);

        } catch (Exception e) {
            throw new Exception("Could not process configuration file: "
                    + e.getMessage());
        }
    }

    /**
     * For each catalog component, identify the highest severity level of any
     * vulnerability by writing that level to a component custom attribute.
     *
     * @throws Exception
     *
     */
    public void process() throws Exception {

        try {
            AbstractAttribute userCustomAttribute = getCustomAttribute();

            log.info("Getting all catalog components...");

            ComponentPageFilter cpf = new ComponentPageFilter();
            cpf.setFirstRowIndex(0);
            cpf.setLastRowIndex(Integer.MAX_VALUE);

            List<Component> components = ccWrapper.getInternalApiWrapper()
                    .getColaApi().searchCatalogComponents("", cpf);

            log.info("Found {} components", components.size());
            totalComponents = components.size();

            if (components.size() > 0) {
                setAttributeBasedOnSeverity(components, userCustomAttribute);
                displaySummary();
            } else {
                log.warn("No catalog components found, exiting...");
                return;
            }

        } catch (Exception e) {
            throw new Exception("Unable to process: " + e.getMessage());
        }
    }

    /**
     * Gets the custom attribute as specified by the user Inability to get the
     * attribute is considered fatal.
     *
     * @param components
     * @throws Exception
     */
    private AbstractAttribute getCustomAttribute() throws Exception {
        String attribName = config.getCustomAttributeName();

        log.info("Verifying custom attribute with name: " + attribName);

        try {

            AttributeNameToken attribToken = new AttributeNameToken();

            attribToken.setName(attribName);

            AbstractAttribute abstractAttribute = ccWrapper
                    .getInternalApiWrapper().getAttributeApi()
                    .getAttribute(attribToken);

            if (abstractAttribute == null) {
                throw new Exception(
                        "Unable to find custom attribute with name: "
                                + attribName);
            }

            return abstractAttribute;

        } catch (SdkFault e) {
            throw new Exception("Unable to verify custom attribute presence", e);
        }

    }

    /**
     * @param components
     * @param userCustomAttribute
     */
    private void setAttributeBasedOnSeverity(List<Component> components,
            AbstractAttribute userCustomAttribute) {
        int counter = 1;
        for (Component component : components) {
            log.info("[{}/{}] Processing component: {}", counter,
                    components.size(), component.getName());
            String componentName = component.getName();
            String componentVersion = component.getVersion();
            List<VulnerabilitySummary> vulns = new ArrayList<VulnerabilitySummary>();
            try {

                log.debug("Collecting vulnerabilities for component {} : {}",
                        componentName, componentVersion);
                VulnerabilityPageFilter vpf = new VulnerabilityPageFilter();
                vpf.setFirstRowIndex(0);
                vpf.setLastRowIndex(Integer.MAX_VALUE);

                vulns = ccWrapper
                        .getInternalApiWrapper()
                        .getVulnerabilityApi()
                        .searchDirectMatchedVulnerabilitiesByCatalogComponent(
                                component.getId(), vpf);
            } catch (Exception e) {
                log.error("Unable to get vulnerabilities for component: "
                        + componentName, e);
                continue;
            }

            if (vulns.size() == 0) {
                log.info(
                        "No vulnerabilities found for component {} : {}, resetting",
                        componentName, componentVersion);
                updateComponent("", component, userCustomAttribute);
            } else {
                log.info(
                        "Sorting through '{}' vulnerabilities for component {} : {}",
                        vulns.size(), componentName, componentVersion);

                // If we get to this, find the highest severity
                // The logic is as follows: Keep looking for the highest, if the
                // highest is found (which is 'High', then exit)
                String highestSeverity = findHighestSev(vulns);

                updateComponent(highestSeverity, component, userCustomAttribute);
            }

            counter++;
        }
    }

    /**
     * This actually assigns the value of the custom attributes and performs the
     * save.
     *
     * @param highestSeverity
     * @param component
     * @param userCustomAttribute
     */
    private void updateComponent(String highestSeverity, Component component,
            AbstractAttribute userCustomAttribute) {
        try {
            log.debug("Updating component {} : {}", component.getName(),
                    component.getVersion());

            AttributeValue av = new AttributeValue();
            av.setAttributeId(userCustomAttribute.getId());
            av.getValues().add(highestSeverity);

            ComponentUpdate update = new ComponentUpdate();
            update.setId(component.getId());
            List<AttributeValue> updateAttributeList = update
                    .getAttributeValues();
            updateAttributeList.add(av);

            ccWrapper.getInternalApiWrapper().getColaApi()
                    .updateCatalogComponent(update);

            log.info("Updating component {} : {} with highest severity of: {}",
                    component.getName(), component.getVersion(),
                    highestSeverity);

        } catch (SdkFault e) {
            log.error("Failed to update component {}, cause: {}",
                    component.getName(), e.getMessage());

            failedComponents.add(component.getName() + ":"
                    + component.getVersion());
        }
    }

    /**
     * Check our hashmap to find the value of the severity If the severity is
     * higher, then set it to the current one
     *
     * Break out if 'High' is found.
     *
     * @param vulns
     * @return
     */
    private String findHighestSev(List<VulnerabilitySummary> vulns) {
        String highestSevName = null;
        Integer sevValue = 0;
        try {
            for (VulnerabilitySummary vs : vulns) {
                String severityName = vs.getSeverity().value();
                // Grab the value of this current severity
                Integer tempValue = VULNERABILITY_SEVERITY_MAP.get(severityName
                        .toLowerCase());

                // If it is higher than what we have, then:
                // - Remember the value
                // - Remember the name
                if (tempValue > sevValue) {
                    sevValue = tempValue;
                    highestSevName = severityName;
                }

                // This is to short-circuit this entire loop
                // If we reach the highest severity, then break out
                if (sevValue == HighSevConstants.SEV_VALUE_HIGH) {
                    break;
                }

            }
        } catch (Exception e) {
            log.error("Unable to determine highest severity", e);
        }

        return highestSevName;
    }

    /**
     * Write to the log a summary of what the process() method did.
     */
    public void displaySummary() {
        log.info("Total components processed: " + totalComponents);
        log.info("Total components failed to update: "
                + failedComponents.size());

        if (failedComponents.size() > 0) {
            log.info("List of failed components: ");
            for (String fc : failedComponents) {
                log.info(fc);
            }
        }
    }

}
