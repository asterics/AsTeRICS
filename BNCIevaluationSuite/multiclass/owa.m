function owaResult=owa(dataVector,orderWeightVector)
%----------------------------------------------------------
% StarEEGlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% owa
%     This is a function that compute the OWA operator, proposed by Yager. 
%
%     It pressents
%     the interface in the vectorial form as the fuzzy integral. I.E. 
%     it computes the OWA of M vectors of dimension N with respect to K
%     different sets of weights, therefore the function computes K*M OWAs in a run.
%     Each set of weights is given in a row vector,
%     where the weighting coefficients are
%     organized as following:
%     orderWeightVector=[coeff_(1),coeff_(2),....,coeff_(N)]
%     since N is the dimensions of the vectors to be fused. coeff_(1) applies to the
%     largest vector component, coeff_(2) to the second largest, ..., and coeff_(N) to the
%     smallest
% 
%     This function is in vectorized form. Being
%     vectorized allows using it as vectorized in the GA Toolbox
%
% [OWA operator first described in:
% Ronald R. Yager. 1988. On ordered weighted averaging aggregation operators in
% multicriteria decisionmaking. IEEE Trans. Syst. Man Cybern. 18, 1 (January 1988),
% 183-190.]
%----------------------------------------------------------
%Inputs
% dataVector:	data in matrix form of M vectors to be fused with dimensions N - matrix N x M
% orderWeightVector: K sets of weights of N weighting coefficients - matrix K x N
%         (theoretically weighting coefficients are constrained to sum up 1)
%----------------------------------------------------------
%Outputs
% owaResult: OWA results organized in a matrix - matrix K x M.
%       You find in position (i,j) of this martrix the owa result of vector j 
%       (which was given in dataVector(:,j)) with respect to the set of weights i (whose 
%       coefficients were given in orderWeightVector(i,:)).
%       
%----------------------------------------------------------
%Dependencies
% None
%----------------------------------------------------------
% Version	Date		Author	Changes 
% v1		16/10/10    ASF     First version based on preliminary works for oceanpal data
%----------------------------------------------------------
% EX.
%     We want to compute the minimum, which is equivalent to an owa
%     w.r.t. a set of weights [0,0,0,...,0,1], in the first row and the
%     average (owa w.r.t. [1,1,1,...,1] in the second row of arr
% >> arr
% 
% arr =
% 
%     0.8147    0.9134    0.2785    0.9649
%     0.9058    0.6324    0.5469    0.1576
%     0.1270    0.0975    0.9575    0.9706
% 
%  
%  >> oweights
% 
% oweights =
% 
%      0     0     1
%      1     1     1
%
%  >> owa(arr,oweights)
% Weights have to sum up 1 in OWA. Automate normalization realized.
% 
% ans =
% 
%     0.1270    0.0975    0.2785    0.1576
%     0.6158    0.5478    0.5943    0.6977
%--------------------------------------------------------------


sumWeights=sum(orderWeightVector,2);
%constraint weights have to sum up 1
if any(abs(1.0-sumWeights)>0.005)
    disp('Weights have to sum up 1 in OWA. Automate normalization realized.')
    orderWeightVector=orderWeightVector./repmat(sumWeights,1,size(orderWeightVector,2));
end
    orderedData=sort(dataVector,'descend');
    owaResult=orderWeightVector*orderedData;
end