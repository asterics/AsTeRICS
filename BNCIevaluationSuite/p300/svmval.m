function [y,y1,y2]=svmval(x,xsup,w,b,kernel,kerneloption,span,framematrix,vector,dual)

% USAGE
% [y,y1,y2]=svmval(x,xsup,w,b,kernel,kerneloption,span,framematrix,vector,dual)
%
% svmval computes the prediction of a support vector machine
%       using the kernel function and its parameter for classification
%		  or regression
%
% INPUT
% x    : input data
% xsup : support vector list    
% w    : weight
% kernel : string containing the type of kernel
% kerneloption : setting parameter of kernel.
% b   : bias. this can be a column vector in case of semiparametric SVM
% span : span matrix for semiparametric SVM
%
%   ----- 1D Frame Kernel -------------------------- 
%
%   framematrix  frame elements for frame kernel
%   vector       sampling position of frame elements
%	 dual 		  dual frame
%
% OUTPUT
%
% y : the output ouf the network at point (vector or matrix) x
%
%       y = w phi(x) - b*span(x)
%		  y1= w phi(x)	
%		  y2=	b*span(x)
%             
%
%	See also svmclass,svmreg, svmkernel
%
%

%	12/10/00 A. Rakotomamonjy Including SVM kernel


%
% Usual verifications
%
semiparam=0;
if nargin<4
    error('Insufficients number of input arguments....');
end;
if nargin < 5
    kernel='gaussian'; 
end;
if nargin < 6
    kerneloption=1; 
end;
if nargin <7
    span=[];
end;
if ~isempty(span)
    semiparam=1;
end;
if ~strcmp(kernel,'frame') | nargin<8;
    framematrix=[];
    vector=[];
end;
if nargin <10
    dual=[];
end;

% [nl nc] = size(x);
% if~isstruct(xsup)
%     [nsup nd] =  size(xsup);
%     if nc ~= nd
%         error('x and xsup must have the same number of column')
%     end
%     
%     
% end;

if~isstruct(xsup)
        [nsup nd] =  size(xsup);
    else
        nsup=length(xsup.indice);
        nd= xsup.dimension;
end;

if~isstruct(x)
        [nl nc] =  size(x);
    else
        nl=length(x.indice);
        nc= x.dimension;
end;
if nc ~= nd
        error('x and xsup must have the same number of column')
 end


%
%
%
%keyboard
% these is a chunking procedure if number of sv is too large
% or number of data to test is to large.
if  ~strcmp(kernel,'numerical')& ~isstruct(x) & ~isstruct(xsup) &(nl > 1000 | nsup > 1000)  ;
    if ~isempty(w)
        chunksize=100;
        chunks1=ceil(nsup/chunksize);
        chunks2=ceil(nl/chunksize);
        y2=zeros(nl,1);
        for ch1=1:chunks1
            ind1=(1+(ch1-1)*chunksize) : min( nsup, ch1*chunksize);
            
            for ch2=1:chunks2
                ind2=(1+(ch2-1)*chunksize) : min(nl, ch2*chunksize);
                kchunk=svmkernel(x(ind2,:),kernel,kerneloption,xsup(ind1,:));
                
                y2(ind2)=y2(ind2)+ kchunk*w(ind1) ;
            end;
        end
        if semiparam
            y1=span*b;
            y=y1+y2;
        else
            % keyboard
            y=y2+b;
        end;
    else
        y=[];
    end;
    
elseif isfield(xsup,'datafile') | isfield(x,'datafile');  % data is stored in file and not in memory
    
    if isstruct(xsup);
        nsup=length(xsup.indice);
    else
        nsup=size(xsup,1);
    end;
    if isstruct(x);
        nl=length(x.indice);
    else
        nl=size(x,1);
    end;
    
    chunksize=100;
    chunks1=ceil(nsup/chunksize);
    chunks2=ceil(nl/chunksize);
    y2=zeros(nl,1);
    for ch1=1:chunks1
        ind1=(1+(ch1-1)*chunksize) : min( nsup, ch1*chunksize);
        
        for ch2=1:chunks2
            ind2=(1+(ch2-1)*chunksize) : min(nl, ch2*chunksize);
            
            %-----------------------------------------------------------                
            if ~isfield(x,'datafile')
                x1=x(ind2,:);
            else
                x1=fileaccess(x.datafile,x.indice(ind2),x.dimension);
            end;   
            if ~isfield(xsup,'datafile')
                x2=xsup(ind1,:);
            else
                x2=fileaccess(xsup.datafile,xsup.indice(ind1),xsup.dimension);
            end;   
            kchunk=svmkernel(x1,kernel,kerneloption,x2);
            %kchunk=svmkernel(x(ind2,:),kernel,kerneloption,xsup(ind1,:));
            
            y2(ind2)=y2(ind2)+ kchunk*w(ind1) ;
        end;
    end
    if semiparam
        y1=span*b;
        y=y1+y2;
    else
        % keyboard
        y=y2+b;
    end;
    
else
    ps=svmkernel(x,kernel,kerneloption,xsup,framematrix,vector,dual);
    
    
    if semiparam
        
        y1=span*b;
        if isempty(w)
            y=y1;
            y2=zeros(size(y1));
            
        else
            y2=ps*w;
            y = y1+y2;
        end;
    else
        y=ps*w+b;
    end;
end;
