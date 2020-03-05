package com.thomsonreuters.uscl.ereader.deliver.service;

import lombok.Getter;

@Getter
public enum ProviewStatus {
    Review(1), Final(2), Removed(3), Cleanup(4);

    int priority;

    ProviewStatus(int priority) {
        this.priority = priority;
    }

}
