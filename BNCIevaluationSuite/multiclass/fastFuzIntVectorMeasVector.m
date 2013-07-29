function fuzInt=fastFuzIntVectorMeasVector(arr,muMeas)
%----------------------------------------------------------
% StarEEGlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% fastFuzIntVectorMeasVector
%     Compute fuzzy integrals of M vectors of dimension N with respect to K
%     different fuzzy measures. I.e. the function computes K*M integrals in a run.
%     Each fuzzy measure is given in a row vector,
%     where the coefficints of the measures are
%     organized as following:
%     muMeas_i=[coeff_1,coeff_2,coeff_1_2,coeff_3,coeff_1_3,coeff_2_3,coeff_1_2_3,coeff_4,....,coeff_1_2_3_4_..._N]
%     since N is the dimensions of the vectors to be fused and therefore 
%     the fuzzy measures present 2^(N-1) coefficients.
% 
%     This function is in vectorized form. Being
%     vectorized allows using it as vectorized in the GA Toolbox
%
% [Fast Fuzzy integral algorithm as described in:
% Aggregation Functions: A Guide for Practitioners
% Beliakov, Pradera, and Calvo
% Studies in Fuzziness and Soft Computing, Vol. 221, 2007
% ISBN: 978-3-540-73720-9]
%----------------------------------------------------------
%Inputs
% arr:	data in matrix form of M vectors to be fused with dimensions N - matrix N x M
% muMeas: K fuzzy measures of 2^(N-1) coefficients - matrix K x 2^(N-1)
%         (theoretically fuzzy measure coefficients are defined in the interval [0,inf],
%           and regular fuzzy measures, which are the most used ones, in [0,1])
%----------------------------------------------------------
%Outputs
% fuzInt: Fuzzy integral results organized in a matrix - matrix K x M.
%       You find in position (i,j) of this martrix the fuzzy integral result of vector j 
%       (which was given in arr(:,j)) with respect to the fuzzy measure i (whose 
%       coefficients were given in muMeas(i,:)).
%       
%----------------------------------------------------------
%Dependencies
% None
%----------------------------------------------------------
% Version	Date		Author	Changes 
% v1		15/10/10    ASF     First version based on preliminary works for oceanpal data
%----------------------------------------------------------
% EX.
%     We want to compute the minimum, which is equivalent to a fuzzy integral
%     w.r.t. a fuzzy measure [0,0,0,...,0,1], in the first row and the
%     maximum (fuzzy integral w.r.t. [1,1,1,...,1] in the second row of arr
% >> arr
% 
% arr =
% 
%     0.1536    0.2525    0.3962    0.2047
%     0.0036    0.3400    0.9819    0.0141
%     0.9386    0.0077    0.0020    0.8546
% 
% >> muMeas
% 
% muMeas =
% 
%      0     0     0     0     0     0     1
%      1     1     1     1     1     1     1
% 
% >> fuzInt=fastFuzIntVectorMeasVector(arr,muMeas)
% 
% fuzInt =
% 
%     0.0036    0.0077    0.0020    0.0141
%     0.9386    0.3400    0.9819    0.8546
%--------------------------------------------------------------

[sortArr,sortInd]=sort(arr);
muInd=repmat((2^size(arr,1))-1,1,size(arr,2));
fuzInt=repmat(sortArr(1,:),size(muMeas,1),1).*muMeas(:,muInd);
for i=2:size(arr,1)
    muInd=muInd-2.^(sortInd(i-1,:)-1);
    fuzInt=fuzInt+(repmat(sortArr(i,:)-sortArr(i-1,:),size(muMeas,1),1)).*muMeas(:,muInd);
end