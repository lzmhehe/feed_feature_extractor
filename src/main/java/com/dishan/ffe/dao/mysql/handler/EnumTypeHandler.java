// package com.dishan.ffe.dao.handler;
//
//
// import java.sql.CallableStatement;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.util.Map;
// import java.util.function.Function;
// import java.util.stream.Collectors;
// import java.util.stream.Stream;
//
// import org.apache.ibatis.type.BaseTypeHandler;
// import org.apache.ibatis.type.JdbcType;
//
// /**
//  * Mybatis枚举类型处理器
//  *
//  * <p>如果枚举类型是<code>com.digitforce.springframework.common.CodeEnum</code>的子类，
//  * 则代理<code>com.digitforce.springframework.common.EnumTypeHandler.CodeEnumTypeHandler</code></p>
//  *
//  * <p>否则，代理<code>org.apache.ibatis.type.EnumTypeHandler</code></p>
//  *
//  * @param <E> 枚举类型
//  * @see CodeEnumTypeHandler
//  * @see org.apache.ibatis.type.EnumTypeHandler
//  */
// public class EnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
//
//     private final BaseTypeHandler<E> delegate;
//
//     public EnumTypeHandler(Class<E> type) {
//         if (type == null) {
//             throw new IllegalArgumentException("Type argument cannot be null");
//         }
//         delegate = new org.apache.ibatis.type.EnumTypeHandler<>(type);
//     }
//
//     @Override
//     public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
//         delegate.setNonNullParameter(ps, i, parameter, jdbcType);
//     }
//
//     @Override
//     public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
//         return delegate.getNullableResult(rs, columnName);
//     }
//
//     @Override
//     public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//         return delegate.getNullableResult(rs, columnIndex);
//     }
//
//     @Override
//     public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//         return delegate.getNullableResult(cs, columnIndex);
//     }
//
//     private static class CodeEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {
//
//         private final Map<Integer, E> codeEnumMap;
//
//         public CodeEnumTypeHandler(Class<E> type) {
//             codeEnumMap = Stream.of(type.getEnumConstants())
//                     .collect(Collectors.toMap(e -> ((CodeEnum) e).getCode(), Function.identity()));
//         }
//
//         @Override
//         public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
//             ps.setInt(i, ((CodeEnum) parameter).getCode());
//         }
//
//         @Override
//         public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
//             int code = rs.getInt(columnName);
//             return rs.wasNull() ? null : codeEnumMap.get(code); //注意wasNull()方法应在rs.getXXX()之后调用
//         }
//
//         @Override
//         public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
//             int code = rs.getInt(columnIndex);
//             return rs.wasNull() ? null : codeEnumMap.get(code); //注意wasNull()方法应在rs.getXXX()之后调用
//         }
//
//         @Override
//         public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
//             int code = cs.getInt(columnIndex);
//             return cs.wasNull() ? null : codeEnumMap.get(code); //注意wasNull()方法应在cs.getXXX()之后调用
//         }
//     }
// }