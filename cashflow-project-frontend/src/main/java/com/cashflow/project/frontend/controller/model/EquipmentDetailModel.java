package com.cashflow.project.frontend.controller.model;

import com.cashflow.entitydomains.FileUrl;
import com.cashflow.fixedasset.domain.FixedAsset;
import com.cashflow.fixedasset.domain.FixedAssetSubCategories;
import com.cashflow.project.domain.project.expense.BillableType;
import com.anosym.common.Amount;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * 
 * @since 14 Dec, 2016, 10:41:34 AM
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentDetailModel implements Serializable {

    private static final long serialVersionUID = -9087623148729044997L;

    @Nullable
    private Calendar equipmentDate = Calendar.getInstance();

    @Nullable
    private String equipmentCategory;

    @Nullable
    private String equipmentSubCategory;

    @Nullable
    private String equipmentName;

    @Nullable
    private String equipmentId;

    @Nullable
    private Amount costRate;

    @Nullable
    private BigDecimal billRate = BigDecimal.ZERO;

    private int unit = 0;

    @Nullable
    private Amount equipmentAmount;

    @Nullable
    private String uuid;

    @Nullable
    private FixedAsset ownedEquipment;

    @Nullable
    private BillableType billable = BillableType.YES;

    @Nonnull
    private List<FixedAssetSubCategories> subCatList = new ArrayList<>();

    @Nonnull
    private List<FixedAsset> equipmentList = new ArrayList<>();

    @Nonnull
    private List<FileUrl> equipmentDocs = new ArrayList<>();

    @Nullable
    private HashMap<String, InputStream> inputStreams = new HashMap<>();

}
