package cn.edu.hfuu.iao.betAndRun.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;

import org.marmakoide.es.distributions.EDistribution;
import org.marmakoide.mlp.ETransferFunction;

import cn.edu.hfuu.iao.betAndRun.IDescribable;
import cn.edu.hfuu.iao.betAndRun.budget.BudgetDivision;
import cn.edu.hfuu.iao.betAndRun.budget.policies.Continue1FromN;
import cn.edu.hfuu.iao.betAndRun.data.transformation.PointSelector;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.IDecisionMaker;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.models.MLPModel;
import cn.edu.hfuu.iao.betAndRun.decisionMakers.simple.CurrentBest;

/** reflection utils */
public class ReflectionUtils {

  /** the packages */
  private static final String[] PACKAGES = {
      (IDescribable.class.getPackage().getName() + '.'), //
      (PointSelector.class.getPackage().getName() + '.'), //
      (BudgetDivision.class.getPackage().getName() + '.'), //
      (Continue1FromN.class.getPackage().getName() + '.'), //
      (IDecisionMaker.class.getPackage().getName() + '.'), //
      (MLPModel.class.getPackage().getName() + '.'), //
      (CurrentBest.class.getPackage().getName() + '.'), //
      (EDistribution.class.getPackage().getName() + '.'), //
      (ETransferFunction.class.getPackage().getName() + '.'), //
  };

  /**
   * get the class
   *
   * @param string
   *          the string
   * @return the class
   * @throws Throwable
   *           if it goes wrong
   */
  @SuppressWarnings("incomplete-switch")
  private static final Class<?> __clazz(final String string)
      throws Throwable {
    final String trimmed = string.trim();
    try {
      return Class.forName(trimmed);
    } catch (final Throwable xerror) {
      for (final String pack : ReflectionUtils.PACKAGES) {
        try {
          return Class.forName(pack + trimmed);
        } catch (final Throwable error2) {
          xerror.addSuppressed(error2);
        }
      }

      switch (trimmed.toLowerCase()) {
        case "byte": {//$NON-NLS-1$
          return byte.class;
        }
        case "short": {//$NON-NLS-1$
          return short.class;
        }
        case "int": {//$NON-NLS-1$
          return int.class;
        }
        case "long": {//$NON-NLS-1$
          return long.class;
        }
        case "float": {//$NON-NLS-1$
          return float.class;
        }
        case "double": {//$NON-NLS-1$
          return double.class;
        }
        case "boolean": {//$NON-NLS-1$
          return boolean.class;
        }
        case "char": {//$NON-NLS-1$
          return char.class;
        }
        case "byte[]": {//$NON-NLS-1$
          return byte[].class;
        }
        case "short[]": {//$NON-NLS-1$
          return short[].class;
        }
        case "int[]": {//$NON-NLS-1$
          return int[].class;
        }
        case "long[]": {//$NON-NLS-1$
          return long[].class;
        }
        case "float[]": {//$NON-NLS-1$
          return float[].class;
        }
        case "double[]": {//$NON-NLS-1$
          return double[].class;
        }
        case "boolean[]": {//$NON-NLS-1$
          return boolean[].class;
        }
        case "char[]": {//$NON-NLS-1$
          return char[].class;
        }

      }

      throw xerror;
    }
  }

  /**
   * Create an object from a string
   *
   * @param string
   *          the string
   * @return the object
   */
  public static final Object createFromString(final String string) {
    final String[] split = string.trim().split("\\s+"); //$NON-NLS-1$

    try {
      Class<?> clazz;

      clazz = ReflectionUtils.__clazz(split[0].trim());

      final ArrayList<Class<?>> paramClasses = new ArrayList<>();
      final ArrayList<Object> values = new ArrayList<>();

      for (int index = 1; index < split.length; index++) {
        final String value = split[index].trim();
        final int find = value.indexOf('(');

        final Class<?> pclazz = ReflectionUtils
            .__clazz(value.substring(0, find).trim());

        paramClasses.add(pclazz);
        final String pvalue = value.substring(find + 1, value.length() - 1)
            .trim();

        if ((byte.class == pclazz) || //
            (byte.class.isAssignableFrom(pclazz)) || //
            (Byte.class.isAssignableFrom(pclazz))) {
          values.add(Byte.valueOf(pvalue));
          continue;
        }
        if ((short.class == pclazz) || //
            (short.class.isAssignableFrom(pclazz)) || //
            (Short.class.isAssignableFrom(pclazz))) {
          values.add(Short.valueOf(pvalue));
          continue;
        }
        if ((int.class == pclazz) || //
            (int.class.isAssignableFrom(pclazz)) || //
            (Integer.class.isAssignableFrom(pclazz))) {
          values.add(Integer.valueOf(pvalue));
          continue;
        }
        if ((long.class == pclazz) || //
            (long.class.isAssignableFrom(pclazz)) || //
            (Long.class.isAssignableFrom(pclazz))) {
          values.add(Long.valueOf(pvalue));
          continue;
        }
        if ((float.class == pclazz) || //
            (float.class.isAssignableFrom(pclazz)) || //
            (Float.class.isAssignableFrom(pclazz))) {
          values.add(Float.valueOf(pvalue));
          continue;
        }
        if ((double.class == pclazz) || //
            (double.class.isAssignableFrom(pclazz)) || //
            (Double.class.isAssignableFrom(pclazz))) {
          values.add(Double.valueOf(pvalue));
          continue;
        }
        if ((boolean.class == pclazz) || //
            (boolean.class.isAssignableFrom(pclazz)) || //
            (Boolean.class.isAssignableFrom(pclazz))) {
          values.add(Boolean.valueOf(pvalue));
          continue;
        }
        if ((char.class == pclazz) || //
            (char.class.isAssignableFrom(pclazz)) || //
            (Character.class.isAssignableFrom(pclazz))) {
          values.add(Character.valueOf(pvalue.charAt(0)));
          continue;
        }
        if (pclazz.isEnum()
            || (PointSelector.class.isAssignableFrom(pclazz))) {
          Object v = pclazz.getMethod("valueOf", //$NON-NLS-1$
              String.class).invoke(null, pvalue);
          if (!(pclazz.isEnum())) {
            v = Cache.canonicalize(v);
          }
          values.add(v);
          continue;
        }

        if (pclazz.isArray()) {
          final Class<?> cclass = pclazz.getComponentType();
          if (pvalue.length() <= 0) {
            final Object array = Array.newInstance(cclass, 0);
            values.add(array);
            continue;
          }
          final String[] vv = pvalue.split("\\|");//$NON-NLS-1$
          final Object array = Array.newInstance(cclass, vv.length);
          values.add(array);
          for (int arrayIndex = 0; arrayIndex < vv.length; arrayIndex++) {
            if ((byte.class == cclass) || //
                (byte.class.isAssignableFrom(cclass)) || //
                (Byte.class.isAssignableFrom(cclass))) {
              Array.set(array, arrayIndex, Byte.valueOf(vv[arrayIndex]));
              continue;
            }
            if ((short.class == cclass) || //
                (short.class.isAssignableFrom(cclass)) || //
                (Short.class.isAssignableFrom(cclass))) {
              Array.set(array, arrayIndex, Short.valueOf(vv[arrayIndex]));
              continue;
            }
            if ((int.class == cclass) || //
                (int.class.isAssignableFrom(cclass)) || //
                (Integer.class.isAssignableFrom(cclass))) {
              Array.set(array, arrayIndex,
                  Integer.valueOf(vv[arrayIndex]));
              continue;
            }
            if ((long.class == cclass) || //
                (long.class.isAssignableFrom(cclass)) || //
                (Long.class.isAssignableFrom(cclass))) {
              Array.set(array, arrayIndex, Long.valueOf(vv[arrayIndex]));
              continue;
            }
            if ((float.class == cclass) || //
                (float.class.isAssignableFrom(cclass)) || //
                (Float.class.isAssignableFrom(cclass))) {
              Array.set(array, arrayIndex, Float.valueOf(vv[arrayIndex]));
              continue;
            }
            if ((double.class == cclass) || //
                (double.class.isAssignableFrom(cclass)) || //
                (Double.class.isAssignableFrom(cclass))) {
              Array.set(array, arrayIndex, Double.valueOf(vv[arrayIndex]));
              continue;
            }
            if ((boolean.class == cclass) || //
                (boolean.class.isAssignableFrom(cclass)) || //
                (Boolean.class.isAssignableFrom(cclass))) {
              Array.set(array, arrayIndex,
                  Boolean.valueOf(vv[arrayIndex]));
              continue;
            }
            if ((char.class == cclass) || //
                (char.class.isAssignableFrom(cclass)) || //
                (Character.class.isAssignableFrom(cclass))) {
              Array.set(array, arrayIndex,
                  Character.valueOf(vv[arrayIndex].charAt(0)));
              continue;
            }

            throw new IllegalArgumentException(value);
          }
          continue;
        }

        throw new IllegalArgumentException(value);
      }

      return Cache.canonicalize(clazz
          .getConstructor(
              paramClasses.toArray(new Class[paramClasses.size()]))
          .newInstance(values.toArray()));
    } catch (final Throwable error) {
      throw new RuntimeException(error);
    }
  }

}
