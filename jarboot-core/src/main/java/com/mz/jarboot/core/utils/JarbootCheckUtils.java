package com.mz.jarboot.core.utils;

/**
 * Utils for checks
 * @author jianzhengma
 * 以下代码基于开源项目Arthas适配修改
 */
public class JarbootCheckUtils {

    /**
     * check whether a component is in an Array<br/>
     *
     * @param e   component
     * @param s   array
     * @param <E> component type
     * @return <br/>
     * (1,1,2,3)        == true
     * (1,2,3,4)        == false
     * (null,1,null,2)  == true
     * (1,null)         == false
     */
    public static <E> boolean isIn(E e, E... s) {

        if (null != s) {
            for (E es : s) {
                if (isEquals(e, es)) {
                    return true;
                }
            }
        }

        return false;

    }

    /**
     * check whether two components are equal<br/>
     *
     * @param src    source component
     * @param target target component
     * @param <E>    component type
     * @return <br/>
     * (null, null)    == true
     * (1L,2L)         == false
     * (1L,1L)         == true
     * ("abc",null)    == false
     * (null,"abc")    == false
     */
    public static <E> boolean isEquals(E src, E target) {

        return null == src
                && null == target
                || null != src
                && null != target
                && src.equals(target);

    }
    private JarbootCheckUtils() {}
}
