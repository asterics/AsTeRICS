function [word,voteMat]=TestWordAsterics(ypred,codetest,nbiter,method,nAvg)


% USAGE
%
% [word,vote]=TestWord(ypred,codetest,nbiter,method)
%
% low level function for recognizing a single character
% from the classification of post-stimulus signal
%
% ypred     vector of classification of post stimulus
% codetest  row or columns matrix speller code associated to the post
%           stimulus signal
% nbiter    nb of sequences to used for recognition
% method   different methods for defining the predicted character from the
%          several post-stimulus signal class prediction
%          usually 'sum' ( 'maxvote' is an alternative)
% npos      number of positive items / flashes where the stimuly was
%           presented
% neg       number of negative items / flashes where the stimuly was no
%           presented


%matrix=['ABCDEF';'GHIJKL';'MNOPQR';'STUVWX';'YZ1234';'567890'];
%matrix = ['ABC';'DEF';'GHI'];
matrix = ['ADG';'BEH';'CFI'];
nbcol=3;
nblig=3;
npos = 2;
nneg = nbcol+nblig-npos;
nbbloc=90;
word='';
nblettre=length(ypred)/nbbloc;
% we positionate ourself on the offset corresponding to the data
offsetIter = (nbiter-1)*npos + 1;
voteMat=zeros(nblettre,nbcol,nblig);
for i=1:nblettre
    offsetInd = [];
    indlettre=nbbloc*(i-1)+1:nbbloc*i;
    for j=1:nAvg
        bPos = offsetIter + (npos*(j-1));
        bNeg = offsetIter + (nbbloc*npos/(nneg+npos))+(nneg*(j-1));
        offsetInd = [offsetInd bPos bPos+1 bNeg bNeg+1 bNeg+2 bNeg+3];
    end
    offsetInd = offsetInd(offsetInd<=length(indlettre));
    indlettre=indlettre(offsetInd);
    codelettre=codetest(indlettre);
    yplettre=ypred(indlettre);
    vote=zeros(1,nblig+nbcol);
    voteMat1 = zeros(nbcol,nblig);
    for k=1:length(indlettre)
        
        % MAXVOTE
        switch method
        case 'maxvote'
            if yplettre(k)>0
                vote(codelettre(k))=vote(codelettre(k))+1;    
            end
        case 'sum'
            
            vote(codelettre(k))=vote(codelettre(k))+yplettre(k);   
        end
        fooMat = zeros(nbcol,nblig);
        if codelettre(k)<=nbcol
            fooMat(codelettre(k),:) = vote(codelettre(k));
        else
            fooMat(:,codelettre(k)-nbcol) = vote(codelettre(k));
        end
        voteMat1=voteMat1+fooMat;
        
    end;
    
    [aux,col]=max(vote(1:nbcol));
    [aux,lig]=max(vote(nbcol+1:nbcol+nblig)); 

    voteMat(i,:,:) = voteMat1;
    word= [word matrix(lig,col)];

end;
