package com.cashflow.project.frontend.controller.model;

import java.io.Serializable;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since 26 Dec, 2016, 12:38:04 PM
 */
@Getter
@Setter
public class SuccessMessageModel implements Serializable {

    private static final long serialVersionUID = 9073979186303863009L;

    @Nullable
    private String successMessage;

}
