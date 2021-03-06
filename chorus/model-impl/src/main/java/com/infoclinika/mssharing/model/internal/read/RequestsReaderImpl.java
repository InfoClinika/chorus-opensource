/*
 * C O P Y R I G H T   N O T I C E
 * -----------------------------------------------------------------------
 * Copyright (c) 2011-2012 InfoClinika, Inc. 5901 152nd Ave SE, Bellevue, WA 98006,
 * United States of America.  (425) 442-8058.  http://www.infoclinika.com.
 * All Rights Reserved.  Reproduction, adaptation, or translation without prior written permission of InfoClinika,
 * Inc. is prohibited.
 * Unpublished--rights reserved under the copyright laws of the United States.  RESTRICTED RIGHTS LEGEND Use,
 * duplication or disclosure by the
 */
package com.infoclinika.mssharing.model.internal.read;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSortedSet;
import com.infoclinika.mssharing.model.internal.entity.CopyProjectRequest;
import com.infoclinika.mssharing.model.internal.entity.Instrument;
import com.infoclinika.mssharing.model.internal.entity.Util;
import com.infoclinika.mssharing.model.internal.repository.CopyProjectRequestRepository;
import com.infoclinika.mssharing.model.read.RequestsReader;
import com.infoclinika.mssharing.platform.entity.InstrumentCreationRequestTemplate;
import com.infoclinika.mssharing.platform.entity.LabCreationRequestTemplate;
import com.infoclinika.mssharing.platform.entity.ProjectSharingRequestTemplate;
import com.infoclinika.mssharing.platform.entity.UserLabMembershipRequestTemplate;
import com.infoclinika.mssharing.platform.model.impl.read.DefaultRequestsReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.FluentIterable.from;

/**
 * @author Stanislav Kurilin
 */
@Service("requestsReader")
@Transactional(readOnly = true)
public class RequestsReaderImpl extends
    DefaultRequestsReader<Instrument, ProjectSharingRequestTemplate, UserLabMembershipRequestTemplate,
        LabCreationRequestTemplate, InstrumentCreationRequestTemplate>
    implements RequestsReader {

    public static final Comparator<ProjectCopyRequest> PROJECT_COPY_REQUEST_COMPARATOR =
        new Comparator<ProjectCopyRequest>() {
            @Override
            public int compare(ProjectCopyRequest o1, ProjectCopyRequest o2) {
                return o1.compareTo(o2);
            }
        };

    @Inject
    private CopyProjectRequestRepository copyProjectRequestRepository;

    @Override
    public ImmutableSortedSet<ProjectCopyRequest> myCopyProjectInbox(Long actor) {
        final List<CopyProjectRequest> requests =
            copyProjectRequestRepository.findByReceiver(Util.USER_FROM_ID.apply(actor));
        return from(requests).transform(input -> new ProjectCopyRequest(
            input.getId(),
            input.getSender().getFullName(),
            input.getSender().getId(),
            input.getSender().getEmail(),
            input.getReceiver().getId(),
            input.getProject().getId(),
            input.getProject().getName(),
            input.getDateSent()
        )).toSortedSet(PROJECT_COPY_REQUEST_COMPARATOR);
    }
}
