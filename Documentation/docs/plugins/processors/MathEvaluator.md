##

## Math Evaluator

# Math Evaluator

### Component Type: Processor (Subcategory: Basic Math)

The math evaluator is a component with four inputs. These inputs can be combined in a mathematical expression which is entered via a property of the component. The expression parser used is [JEPlite2](http://sourceforge.net/projects/jeplite/) which supports arithmetic as well as numerous mathematical expressions.

The list of supported mathematical functions is sin(), cos(), tan(), asin(), ,acos(), atan(), sqrt(), log(), ln(), angle(), abs(), mod(), sum(), rand(), umin(), add().

![Screenshot: MathEvaluator plugin](./img/MathEvaluator.jpg "Screenshot: MathEvaluator plugin")  
MathEvaluator plugin

## Requirements

The component depends on the JEPlite library which is included in the component's JAR file.

## Input Port Description

- **inA to inD \[double\]:** The inputs which can be accessed in the mathematical expression via a to d. **These 4 input ports support synchronization**

## Output Port Description

- **out \[double\]:** the result of the expression.

## Properties

- **expression \[string\]:** Mathematical expression to be evaluated.
