import Ice
import CalcModule


class CalculatorI(CalcModule.Calculator):

    def divide(self, a: float, b: float, current=None):
        if b == 0.0:
            raise CalcModule.ZeroDivisionErr(reason="Division by zero is not allowed")
        return a / b

    def subtract(self, a: float, b: float, current=None):
        return a - b

    def map(self, nums: list[int], current=None):
        if -1 in nums:
            raise CalcModule.ZeroDivisionErr(reason="Division by zero is not allowed")
        return [1 / (x + 1) for x in nums]

    def getDescription(self, current=None):
        return "Calculator service (ICE)"


communicator = Ice.initialize()
adapter = communicator.createObjectAdapterWithEndpoints("CalculatorAdapter", "tcp -h 127.0.0.2 -p 10000")
calculator = CalculatorI()
identity = Ice.Identity(name="calc11", category="calc")
adapter.add(calculator, identity)
adapter.activate()
communicator.waitForShutdown()
