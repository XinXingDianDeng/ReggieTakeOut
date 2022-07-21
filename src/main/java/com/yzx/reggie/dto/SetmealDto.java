package com.yzx.reggie.dto;

import com.yzx.reggie.entity.Setmeal;
import com.yzx.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
