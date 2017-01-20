package com.cashflow.project.frontend.controller.model;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 
 * @since 30 Nov, 2016, 10:50:59 AM
 */
@Getter
@Setter
public class TaskDepositModel implements Serializable {

    private static final long serialVersionUID = -1796745947886560584L;

    @Nullable
    private BigDecimal depositOrRetainers = BigDecimal.ZERO;

}
