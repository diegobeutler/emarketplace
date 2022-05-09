package br.edu.utfpr.emarketplace.service.impl;

import br.edu.utfpr.emarketplace.enumeration.Operacao;
import br.edu.utfpr.emarketplace.enumeration.Status;
import br.edu.utfpr.emarketplace.exception.RecursoNaoPermitidoException;
import br.edu.utfpr.emarketplace.model.Anuncio;
import br.edu.utfpr.emarketplace.model.ImagemAnuncio;
import br.edu.utfpr.emarketplace.model.Usuario;
import br.edu.utfpr.emarketplace.model.dto.ImagemAnuncioSalvarDto;
import br.edu.utfpr.emarketplace.repository.AnuncioRepository;
import br.edu.utfpr.emarketplace.repository.ImagemAnuncioRepository;
import br.edu.utfpr.emarketplace.repository.criteria.params.AnuncioFilter;
import br.edu.utfpr.emarketplace.service.AnuncioService;
import br.edu.utfpr.emarketplace.service.UsuarioService;
import br.edu.utfpr.emarketplace.service.amazonS3Bucket.AmazonS3BucketServiceImpl;
import br.edu.utfpr.emarketplace.service.email.EnvioEmailServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static br.edu.utfpr.emarketplace.service.utils.ListUtils.isNullOrEmpty;

@RequiredArgsConstructor
@Service
public class AnuncioServiceImpl extends CrudServiceImpl<Anuncio, Long> implements AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final UsuarioService usuarioService;
    private final EnvioEmailServiceImpl envioEmailService;
    private final AmazonS3BucketServiceImpl amazonS3BucketService;
    private final ImagemAnuncioRepository imagemAnuncioRepository;
    private List<ImagemAnuncio> imagensParaExcluir;
    private List<ImagemAnuncioSalvarDto> imagensDataParaSalvar;

    @Override
    public JpaRepository<Anuncio, Long> getRepository() {
        return anuncioRepository;
    }

    @Override
    public void valid(Anuncio entity) {

    }

    @Override
    public void preSave(Anuncio anuncio) {
        updateImagesAws(anuncio);
    }

    @Override
    public Anuncio salvar(Anuncio anuncio) throws Exception {
        if (anuncio.getId() == null) {
            anuncio.setDataPublicacao(LocalDate.now());
            anuncio.setStatus(Status.DISPONIVEL);
            anuncio.setUsuarioOrigem(usuarioService.getUsuarioLogado());
        }

        return save(anuncio);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void postSave(Anuncio anuncio) {
        deleteOldImages();
        saveNewImages(anuncio);
    }

    @Override
    public void excluir(Long id) {
        try {
            imagensParaExcluir = findAnuncioById(id).getImagens();
            delete(id);
            deleteOldImages();
        } catch (RecursoNaoPermitidoException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Anuncio> listarTodos() {
        return null;
    }

    @Override
    public List<Anuncio> findAnunciosByFilter(AnuncioFilter anuncioFilter) {
        var usuarioLogado = usuarioService.getUsuarioLogado();
        anuncioFilter.setUsuarioLogado(usuarioLogado);

        var anuncios = anuncioRepository.findAnunciosByFilter(anuncioFilter);
        setUsuarioLogadoEh(anuncios, usuarioLogado);
        return anuncios;
    }

    private void setUsuarioLogadoEh(List<Anuncio> anuncios, Usuario usuarioLogado) {
        if (Objects.nonNull(usuarioLogado)) {
            anuncios.forEach(e -> setUsuarioLogadoEh(e, usuarioLogado));
        }
    }

    private void setUsuarioLogadoEh(Anuncio anuncio, Usuario usuarioLogado) {
        anuncio.setEhUsuarioOrigem(usuarioLogado.equals(anuncio.getUsuarioOrigem()));
        anuncio.setEhUsuarioDestino(usuarioLogado.equals(anuncio.getUsuarioDestino()));
        anuncio.setEhUsuarioInstituicao(usuarioLogado.equals(anuncio.getUsuarioInstituicao()));
    }

    @Override
    public void convidarInstituicao(String emailInstituicao) {
        this.envioEmailService.convidarInstituicaoEmail(emailInstituicao);
    }

    private void updateImagesAws(Anuncio anuncio) {
        imagensParaExcluir = null;
        imagensDataParaSalvar = null;
        if (anuncio.getId() != null) {
            imagensParaExcluir = getImagesDeleted(anuncio);
        }
        imagensDataParaSalvar = getImagensDataParaSalvar(anuncio.getImagens());
        anuncio.getImagens().removeAll(anuncio.getImagens().stream()// todo serve para remover as imagens em base 64, pois já foram salvas e o caminho está na lista
                .filter(imagemAnuncio -> imagemAnuncio.getUrl().startsWith("data")).toList());
    }

    private List<ImagemAnuncioSalvarDto> getImagensDataParaSalvar(List<ImagemAnuncio> imagens) {
        return imagens.stream().filter(imagemAnuncio -> imagemAnuncio.getUrl().startsWith("data"))
                .map(ImagemAnuncioSalvarDto::new)
                .collect(Collectors.toList());
    }

    private void deleteOldImages() {
        if (!isNullOrEmpty(imagensParaExcluir)) {
            imagensParaExcluir.forEach(imagem -> amazonS3BucketService.deleteFileFromBucket(imagem.getUrl().substring(60, 100), false));
            this.imagemAnuncioRepository.deleteAll(imagensParaExcluir);
        }
    }

    private void saveNewImages(Anuncio anuncio) {
        if (!isNullOrEmpty(imagensDataParaSalvar)) {
            imagensDataParaSalvar.forEach(imagem -> {
                try {
                    var file = new File(UUID.randomUUID() + ".png");
                    BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagem.getImageData()));
                    ImageIO.write(bufferedImage, "png", file);
                    anuncio.getImagens().add(imagemAnuncioRepository.save(ImagemAnuncio.builder()
                            .url(amazonS3BucketService.uploadFile(file, false))
                            .nome(imagem.getNome())
                            .build()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            anuncioRepository.save(anuncio);
        }
    }

    private List<ImagemAnuncio> getImagesDeleted(Anuncio anuncio) {
        var imagensId = anuncio.getImagens().stream()
                .map(ImagemAnuncio::getId)
                .filter(Objects::nonNull).toList();
        var imagensDoBanco = anuncioRepository.findById(anuncio.getId())
                .orElseThrow(() -> new NoSuchElementException("Anúncio não encontrado."))
                .getImagens();
        return imagensDoBanco.stream().filter(img -> !imagensId.contains(img.getId())).toList();
    }

    @Override
    public Anuncio updateStatus(Anuncio anuncio) {
        var anuncioBanco = findById(anuncio.getId());
        var usuarioLogado = usuarioService.getUsuarioLogado();
        switch (anuncioBanco.getStatus()) {
            case DISPONIVEL:
                anuncioBanco.setStatus(Status.EM_NEGOCIACAO);
                if (!Operacao.DOACAO_PRODUTO.equals(anuncioBanco.getOperacao())) {
                    anuncioBanco.setUsuarioDestino(usuarioLogado);
                }
                break;
            case EM_NEGOCIACAO:
                if (Status.DISPONIVEL.equals(anuncio.getStatus())) {
                    anuncioBanco.setStatus(Status.DISPONIVEL);
                    anuncioBanco.setUsuarioDestino(null);
                } else if (Status.FINALIZADO.equals(anuncio.getStatus()) && usuarioLogado.equals(anuncioBanco.getUsuarioOrigem())) {
                    anuncioBanco.setStatus(Status.FINALIZADO);
                }
        }
        anuncioBanco = anuncioRepository.save(anuncioBanco);
        setUsuarioLogadoEh(anuncioBanco, usuarioLogado);
        return anuncioBanco;
    }

    public Anuncio findAnuncioById(Long id) throws RecursoNaoPermitidoException {
        var anuncio = findById(id);
        if (anuncio.getUsuarioOrigem().equals(usuarioService.getUsuarioLogado())) {
            return anuncio;
        }
        throw new RecursoNaoPermitidoException("Usuário autenticado não é o proprietário do anúncio.");
    }
}
