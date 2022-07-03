function [xsup,w,d,pos,timeps,alpha,obj]=svmclass(x,y,c,lambda,kernel,kerneloption,verbose,span, alphainit)
% USAGE [xsup,w,b,pos,timeps,alpha,obj]=svmclass(x,y,c,lambda,kernel,kerneloption,verbose,span, alphainit)
%
% Support vector machine for CLASSIFICATION
% This routine classify the training set with a support vector machine
% using quadratic programming algorithm (active constraints method)
%
% INPUT
%
% Training set
%      x  		: input data 
%      y  		: output data
% parameters
%		c		: Bound on the lagrangian multipliers     
%		lambda		: Conditioning parameter for QP method
%		kernel		: kernel  type. classical kernel are
%
%		Name			parameters
%		'poly'		polynomial degree
%		'gaussian'	gaussian standard deviation
%
%		for more details see svmkernel
% 
%		kerneloption : parameters of kernel
%
%		for more details see svmkernel
%
% 		verbose : display outputs (default value is 0: no display)
%
%     Span    : span matrix for semiparametric learning 
%               This vector is sized Nbapp*Nbbasisfunction where
%               phi(i,j)= f_j(x(i));
%
%
%
% OUTPUT
%
% xsup	coordinates of the Support Vector
% w      weight
% b		bias
% pos    position of Support Vector
% timeps time for processing the scalar product
% alpha  Lagragian multiplier
% obj    Value of Objective function
%
%
% see also svmreg, svmkernel, svmval

%	21/09/97 S. Canu
%	04/06/00 A. Rakotomamonjy   -inclusion of other kernel functions
%	04/05/01 S. Canu            -inclusion of multi-constraint optimization for frame-SVM
%
%       scanu@insa-rouen.fr, alain.rakoto@insa-rouen.fr


if nargin< 9
    alphainit=[];
end;

if nargin < 8 | isempty(span)
    A = y;
    b = 0;
else
    if span==1
        span=ones(size(y));
    end;
    [na,m]=size(span);
    [n un] = size(y);
    if n ~= na
        error('span, x and y  must have the same number of row')
    end
    A = (y*ones(1,m)).*span;
    b = zeros(m,1);
end
if nargin < 7
    verbose = 0;
end

if nargin < 6
    gamma = 1;
end

if nargin < 5
    kernel = 'gaussian';
end

if nargin < 4
    lambda = 0.000000001;
end

if nargin < 3
    c = inf;
end


[n un] = size(y);

if ~isempty(x)
    [nl nc] = size(x);
    if n ~= nl
        error('x and y must have the same number of row')
    end
end;

if min(y) ~= -1
    error(' y must coded: 1 for class one and -1 for class two')
end

if verbose ~= 0 disp('building the distance matrix'); end;

ttt = cputime;

ps  =  zeros(n,n);		
ps=svmkernel(x,kernel,kerneloption);


%----------------------------------------------------------------------
%      monqp(H,b,c) solves the quadratic programming problem:
% 
%    min 0.5*x'Hx - d'x   subject to:  A'x = b  and  0 <= x <= c 
%     x    
%----------------------------------------------------------------------
H =ps.*(y*y'); 
e = ones(size(y));

timeps = cputime - ttt;

if verbose ~= 0 disp('in QP'); end;
if isinf(c)                                                           
    [alpha , lambda , pos] =  monqpCinfty(H,e,A,b,lambda,verbose,x,ps,alphainit);  
else                                                                 
    [alpha , lambda , pos] = monqp(H,e,A,b,c,lambda,verbose,x,ps,alphainit);         
    
end
if verbose ~= 0 disp('out QP'); end;

alphaall=zeros(size(e));
alphaall(pos)=alpha;
obj=-0.5*alphaall'*H*alphaall +e'*alphaall;

if ~isempty(x)
    xsup = x(pos,:);
else
    xsup=[];
end;

ysup = y(pos);



w = (alpha.*ysup);
d = lambda;

if verbose ~= 0  
    disp('max(alpha)') 
    fprintf(1,'%6.2f\n',max(alpha)) 
end 
