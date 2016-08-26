/**
 * mangoerp.com Inc.
 * Copyright (c) 2015 All Rights Reserved.
 */
package org.opencloudb.parser.druid;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.TableStat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author owen
 * @version $$Id: DruidMycatRouteStrategyTest, v 0.1 15/12/10 下午6:53 owen Exp $$
 */
public class DruidMycatRouteStrategyTest {

    public static void main(String[] args) {
        String sql =
                "/*!mycat: sql= select 1 from sm_logistics_number_group where user_id = ? */  update sm_logistics_number_group\n" +
                "  \tset MODIFIER = ?,gmt_modified = now(),\n" +
                "  \t    total_count = (select count(1) from sm_logistics_number where is_deleted = 'n' and user_id = ? and group_id = ?),\n" +
                "  \t    available_count = (select count(1) from sm_logistics_number where is_deleted = 'n' and user_id = ? and status = 0 and group_id = ?),\n" +
                "  \t    cancel_match_count = (select count(1) from sm_logistics_number where is_deleted = 'n' and user_id = ? and status = -1 and group_id = ?),\n" +
                "  \t    cancel_count = (select count(1) from sm_logistics_number where is_deleted = 'n' and user_id = ? and status = -2 and group_id = ?)\n" +
                "  \twhere id = ? and user_id = ?";
        SQLStatementParser parser = null;
        parser = new MySqlStatementParser(sql);

        MycatSchemaStatVisitor visitor = null;
        SQLStatement statement = null;
        //解析出现问题统一抛SQL语法错误
        try {
            statement = parser.parseStatement();
            visitor = new MycatSchemaStatVisitor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        statement.accept(visitor);

        List<List<TableStat.Condition>> mergedConditionList = new ArrayList<List<TableStat.Condition>>();
        if (visitor.hasOrCondition()) {//包含or语句
            //TODO
            //根据or拆分
            mergedConditionList = visitor.splitConditions();
        } else {//不包含OR语句
            mergedConditionList.add(visitor.getConditions());
        }

    }
}
