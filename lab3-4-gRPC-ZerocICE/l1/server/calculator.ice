#ifndef CALC_ICE
#define CALC_ICE

module CalcModule{

    sequence<double> DoubleSeq;
    sequence<int> IntSeq;
    exception GenericError {
        string reason;
    };
    exception ZeroDivisionErr extends GenericError {};
    interface Calculator {
        double divide(double a, double b) throws ZeroDivisionErr;
        double subtract(double a, double b);
        DoubleSeq map(IntSeq nums) throws ZeroDivisionErr;
        idempotent string getDescription();
    };
};

#endif