package com.blackducksoftware.tools.enterprise;

import java.util.Properties;
import java.util.regex.Pattern;

import com.blackducksoftware.tools.common.EntAppNameConfigMgrDelegate;
import com.blackducksoftware.tools.common.EntAppNameConfigurationManager;

public class TestEntAppNameConfigurationManager implements
	EntAppNameConfigurationManager {

    private EntAppNameConfigMgrDelegate entAppNameConfigMgrDelegate;

    public TestEntAppNameConfigurationManager(Properties props) {
	entAppNameConfigMgrDelegate = new EntAppNameConfigMgrDelegate(props);
    }

    @Override
    public String getSeparatorString() {
	return entAppNameConfigMgrDelegate.getSeparatorString();
    }

    @Override
    public String getWithoutDescriptionFormatPatternString() {
	return entAppNameConfigMgrDelegate
		.getWithoutDescriptionFormatPatternString();
    }

    @Override
    public String getWithDescriptionFormatPatternString() {
	return entAppNameConfigMgrDelegate
		.getWithDescriptionFormatPatternString();
    }

    @Override
    public String getAppIdentifierPatternString() {
	return entAppNameConfigMgrDelegate.getAppIdentifierPatternString();
    }

    @Override
    public String getFollowsDescriptionPatternString() {
	return entAppNameConfigMgrDelegate.getFollowsDescriptionPatternString();
    }

    @Override
    public int getNumSuffixes() {
	return entAppNameConfigMgrDelegate.getNumSuffixes();
    }

    @Override
    public String getSuffixPatternString(int suffixIndex) {
	return entAppNameConfigMgrDelegate.getSuffixPatternString(suffixIndex);
    }

    @Override
    public Pattern getAppIdentifierPattern() {
	return entAppNameConfigMgrDelegate.getAppIdentifierPattern();
    }

    @Override
    public Pattern getFollowsDescriptionPattern() {
	return entAppNameConfigMgrDelegate.getFollowsDescriptionPattern();
    }

    @Override
    public Pattern getWithoutDescriptionFormatPattern() {
	return entAppNameConfigMgrDelegate.getWithoutDescriptionFormatPattern();
    }

    @Override
    public Pattern getWithDescriptionFormatPattern() {
	return entAppNameConfigMgrDelegate.getWithDescriptionFormatPattern();
    }

    @Override
    public Pattern getSuffixPattern(int suffixIndex) {
	return entAppNameConfigMgrDelegate.getSuffixPattern(suffixIndex);
    }

}
