package com.teleport.masterdata.dto.packagetype;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestRequestAddPackageTypeDTO {
    @NonNull private String packageTypeName;
}
